package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.testcontainer.RedisTestContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RedisRateLimiterFactoryTest extends RedisTestContainer {
    @Test
    void getInstance() throws IOException {
        RedisRateLimiterFactory redisRateLimiterFactory = new RedisRateLimiterFactory(REDIS_HOST, getRealRedisPort());

        RateLimiter rateLimiter = redisRateLimiterFactory.getInstance();

        assertThat(rateLimiter).isInstanceOf(RedisTokenBucketRateLimiter.class);
        redisRateLimiterFactory.close();
    }
}
