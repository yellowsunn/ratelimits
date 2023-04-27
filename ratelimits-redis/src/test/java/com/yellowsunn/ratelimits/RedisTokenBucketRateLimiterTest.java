package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.extension.RedisStandAloneRegisterExtension;
import com.yellowsunn.ratelimits.tokenbucket.RedisTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

class RedisTokenBucketRateLimiterTest extends AbstractRateLimiterTest {
    @RegisterExtension
    static RedisStandAloneRegisterExtension extension = new RedisStandAloneRegisterExtension();
    TokenBucketRepository tokenBucketRepository;

    @BeforeEach
    void setUp() {
        tokenBucketRepository = new RedisTokenBucketRepository(extension.getRedisCommands(), super.clock);
        rateLimiter = new RedisTokenBucketRateLimiter(tokenBucketRepository, extension.getRedissonClient());
    }
}
