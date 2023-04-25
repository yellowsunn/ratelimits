package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.extension.RedisStandAloneRegisterExtension;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class RedisTokenBucketRepositoryTest {
    @RegisterExtension
    static RedisStandAloneRegisterExtension extension = new RedisStandAloneRegisterExtension();

    RedisTokenBucketRepository redisTokenBucketRepository;

    @BeforeEach
    void setUp() {
        redisTokenBucketRepository = new RedisTokenBucketRepository(extension.getRedisCommands());
    }

    @Test
    void ShouldHave10TokenAmount() {
        // given
        String key = "ip:127.0.0.1";
        redisTokenBucketRepository.saveTokenAmount(key, 10L);

        // when
        Long result = redisTokenBucketRepository.findTokenAmount(key);

        // then
        assertThat(redisTokenBucketRepository.lastModifiedTime(key)).isCloseTo(System.currentTimeMillis() / 1000L, Offset.offset(2L));
        assertThat(result).isEqualTo(10L);
    }
}
