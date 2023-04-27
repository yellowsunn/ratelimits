package com.yellowsunn.ratelimits;

import java.beans.ConstructorProperties;
import java.time.Duration;

public class RedisRateLimitRule extends RateLimitRule {

    @ConstructorProperties({"capacity", "duration"})
    public RedisRateLimitRule(long capacity, Duration duration) {
        super(capacity, duration);
    }
}
