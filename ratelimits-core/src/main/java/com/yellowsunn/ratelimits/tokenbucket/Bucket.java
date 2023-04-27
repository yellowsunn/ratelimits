package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class Bucket {
    private long amount;
    private long lastRefillTime; // Unix time
    private final RateLimitRule rule;
    private Clock clock;

    public Bucket(RateLimitRule rule) {
        this(rule, Clock.systemUTC());
    }

    public Bucket(RateLimitRule rule, Clock clock) {
        this.amount = rule.getCapacity();
        this.clock = clock;
        this.lastRefillTime = Instant.now(clock).getEpochSecond();
        this.rule = rule;
    }

    protected Bucket(long amount, long lastRefillTime, RateLimitRule rule) {
        this.amount = amount;
        this.lastRefillTime = lastRefillTime;
        this.rule = rule;
        this.clock = Clock.systemUTC();
    }

    public boolean tryAcquireToken() {
        long refillAmount = refillAmountByRule(rule);
        if (refillAmount > 0) {
            this.amount = Math.min(amount + refillAmount, rule.getCapacity());
            this.lastRefillTime = getNowEpochSecond();
        }

        if (this.amount <= 0) {
            return false;
        }
        this.amount -= 1;
        return true;
    }

    protected void changeClock(Clock clock) {
        this.clock = clock;
    }

    public long getAmount() {
        return amount;
    }

    public long getLastRefillTime() {
        return lastRefillTime;
    }

    public RateLimitRule getRule() {
        return rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bucket bucket = (Bucket) o;
        return amount == bucket.amount && lastRefillTime == bucket.lastRefillTime && Objects.equals(rule, bucket.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, lastRefillTime, rule);
    }

    private long refillAmountByRule(RateLimitRule rule) {
        long now = getNowEpochSecond();
        if (isInvalidLastRefillTime(now)) {
            this.amount = rule.getCapacity();
        }

        double elapsedTime = (double) now - lastRefillTime;
        return (long) (elapsedTime * rule.getCapacity() / rule.getDuration().getSeconds());
    }

    private boolean isInvalidLastRefillTime(long now) {
        return lastRefillTime <= 0 || lastRefillTime > now;
    }

    private long getNowEpochSecond() {
        return Instant.now(clock).getEpochSecond();
    }
}
