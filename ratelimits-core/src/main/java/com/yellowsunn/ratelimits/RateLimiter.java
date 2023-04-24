package com.yellowsunn.ratelimits;

public interface RateLimiter {
    boolean acquire(String key, RateLimitRule rule);
    boolean resetLimit(String key);
}
