package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.time.DefaultTimeSupplier;
import com.yellowsunn.ratelimits.time.TimeSupplier;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;

import static java.util.Objects.requireNonNull;

public class InMemoryTokenBucketRateLimiter implements RateLimiter {
    private final TokenBucketRepository tokenBucketRepository;
    private final TimeSupplier timeSupplier;

    public InMemoryTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository) {
        this(tokenBucketRepository, new DefaultTimeSupplier());
    }

    public InMemoryTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository,
                                          TimeSupplier timeSupplier) {
        this.tokenBucketRepository = tokenBucketRepository;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public boolean acquire(String key, RateLimitRule rule) {
        requireNonNull(key);
        requireNonNull(rule);
        Long tokenAmount = tokenBucketRepository.findTokenAmount(key);
        if (tokenAmount == null) {
            tokenBucketRepository.saveTokenAmount(key, rule.getCapacity());
            return true;
        }

        tokenAmount = refill(key, rule, tokenAmount);
        if (tokenAmount <= 0L) {
            return false;
        }
        return tokenBucketRepository.decrementTokenAmount(key);
    }

    @Override
    public boolean resetLimit(String key) {
        requireNonNull(key);
        return tokenBucketRepository.deleteKey(key);
    }

    private long refill(String key, RateLimitRule rule, long tokenAmount) {
        long refillAmount = refillAmount(key, rule);
        if (refillAmount > 0) {
            long newAmount = tokenAmount + refillAmount;
            tokenBucketRepository.saveTokenAmount(key, Math.min(tokenAmount + refillAmount, rule.getCapacity()));
            return newAmount;
        }
        return tokenAmount;
    }

    private long refillAmount(String key, RateLimitRule rule) {
        long now = timeSupplier.now();
        Long lastModifiedTime = tokenBucketRepository.lastModifiedTime(key);
        if (lastModifiedTime == null || now < lastModifiedTime) {
            return rule.getCapacity();
        }

        double elapsedTime = (double) now - lastModifiedTime;
        return (long) (elapsedTime * rule.getCapacity() / rule.getDuration().getSeconds());
    }
}
