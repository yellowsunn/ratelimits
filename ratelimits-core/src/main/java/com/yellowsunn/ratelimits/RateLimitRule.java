package com.yellowsunn.ratelimits;

import java.time.Duration;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class RateLimitRule {
    private long capacity;
    private Duration duration;

    public RateLimitRule(long capacity, Duration duration) {
        requireNonNull(duration);
        if (capacity < 0L) {
            throw new IllegalArgumentException("Capacity must be greater than or equal to Zero");
        }
        if (isLessThanSecond(duration)) {
            throw new IllegalArgumentException("Duration must be greater than or equal to 1 second.");
        }
        this.capacity = capacity;
        this.duration = duration;
    }

    public long getCapacity() {
        return capacity;
    }

    public Duration getDuration() {
        return duration;
    }

    private boolean isLessThanSecond(Duration duration) {
        return duration.compareTo(Duration.ofSeconds(1L)) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimitRule that = (RateLimitRule) o;
        return capacity == that.capacity && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, duration);
    }
}
