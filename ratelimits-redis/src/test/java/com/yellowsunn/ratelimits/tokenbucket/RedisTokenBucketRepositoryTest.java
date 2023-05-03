package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.extension.RedisStandAloneRegisterExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

class RedisTokenBucketRepositoryTest extends AbstractTokenBucketRepositoryTest {
    @RegisterExtension
    static RedisStandAloneRegisterExtension extension = new RedisStandAloneRegisterExtension();

    @BeforeEach
    void setUp() {
        tokenBucketRepository = new RedisTokenBucketRepository(extension.getRedisCommands(), super.clock);
    }
}
