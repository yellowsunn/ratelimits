package com.yellowsunn.ratelimits;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import org.redisson.config.Config;

import java.io.IOException;

public class RedisRateLimiterFactory implements RateLimiterFactory {
    private final RateLimiterFactory delegateFactory;

    public RedisRateLimiterFactory() {
        this("127.0.0.1", 6379);
    }

    public RedisRateLimiterFactory(String host, int port) {
        RedisClient redisClient = RedisClient.create(RedisURI.create(host, port));
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("%s:%s", host, port));

        this.delegateFactory = new RedisStandAloneRateLimiterFactory(redisClient, config);
    }

    public RedisRateLimiterFactory(RedisClient redisClient, Config redissonConfig) {
        this.delegateFactory = new RedisStandAloneRateLimiterFactory(redisClient, redissonConfig);
    }

    public RedisRateLimiterFactory(RedisClusterClient redisClusterClient, Config redissonConfig) {
        this.delegateFactory = new RedisClusterRateLimiterFactory(redisClusterClient, redissonConfig);
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
