package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RedisRateLimitRule;

import java.beans.ConstructorProperties;

public class RedisBucket extends Bucket {
    @ConstructorProperties({"amount", "lastRefillTime", "rule"})
    public RedisBucket(long amount, long lastRefillTime, RedisRateLimitRule rule) {
        super(amount, lastRefillTime, rule);
    }
}
