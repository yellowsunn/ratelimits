package com.yellowsunn.ratelimits;

/**
 * The rate limiter adjusts the request rate according to the RateLimitRule.
 */
public interface RateLimiter {
    /**
     * @param key  key
     * @param rule Defined Rate limiting rule
     * @return Return {@code false} if the request key exceeds the limit, otherwise return {@code true}
     */
    boolean acquire(String key, RateLimitRule rule);

    /**
     * @param key key
     * @return Return {@code true} If resetting the limit succeeds, otherwise return {@code false}
     */
    boolean resetLimit(String key);
}
