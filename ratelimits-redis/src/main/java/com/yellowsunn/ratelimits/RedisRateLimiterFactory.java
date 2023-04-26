package com.yellowsunn.ratelimits;

import io.lettuce.core.RedisClient;
import io.lettuce.core.cluster.RedisClusterClient;
import org.redisson.api.RedissonClient;

import java.io.IOException;

public class RedisRateLimiterFactory implements RateLimiterFactory {
    private final RateLimiterFactory delegateFactory;

    public RedisRateLimiterFactory(RedisClient redisClient, RedissonClient redissonClient) {
        this.delegateFactory = new RedisStandAloneRateLimiterFactory(redisClient, redissonClient);
    }

    public RedisRateLimiterFactory(RedisClusterClient redisClusterClient, RedissonClient redissonClient) {
        this.delegateFactory = new RedisClusterRateLimiterFactory(redisClusterClient, redissonClient);
    }

    @Override
    public RateLimiter getInstance() {
        return delegateFactory.getInstance();
    }

    @Override
    public void close() throws IOException {
        delegateFactory.close();
    }
}
