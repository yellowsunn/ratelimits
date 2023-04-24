package com.yellowsunn.ratelimits.tokenbucket;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenBucketRepositoryTest {

    TokenBucketRepository tokenBucketRepository = new InMemoryTokenBucketRepository(10, 5L, TimeUnit.SECONDS);

    @Test
    void ShouldHave10TokenAmount() {
        // given
        String key = "ip:127.0.0.1";
        tokenBucketRepository.saveTokenAmount(key, 10L);

        // when
        Long result = tokenBucketRepository.findTokenAmount(key);

        // then
        assertThat(result).isEqualTo(10L);
    }
}
