package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.testcontainer.RedisTestContainer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RedisRateLimiterFactoryTest extends RedisTestContainer {
    @Test
    void getInstance() throws IOException {
        RedisClient redisClient = RedisClient.create(RedisURI.create(REDIS_HOST, getRealRedisPort()));

        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_HOST + ":" + getRealRedisPort());
        RedissonClient redissonClient = Redisson.create(config);

        RedisRateLimiterFactory redisRateLimiterFactory = new RedisRateLimiterFactory(redisClient, redissonClient);

        RateLimiter rateLimiter = redisRateLimiterFactory.getInstance();

        assertThat(rateLimiter).isInstanceOf(RedisTokenBucketRateLimiter.class);
        redisRateLimiterFactory.close();
    }
}
