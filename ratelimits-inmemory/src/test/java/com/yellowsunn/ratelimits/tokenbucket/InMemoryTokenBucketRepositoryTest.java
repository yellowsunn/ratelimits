package com.yellowsunn.ratelimits.tokenbucket;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenBucketRepositoryTest {

    TokenBucketRepository tokenBucketRepository = new InMemoryTokenBucketRepository(10);

    @Test
    void ShouldHave10TokenAmount() {
        // given
        String key = "ip:127.0.0.1";
        tokenBucketRepository.saveTokenAmount(key, 10L);

        // when
        Long result = tokenBucketRepository.findTokenAmount(key);

        // then
        assertThat(tokenBucketRepository.lastModifiedTime(key)).isCloseTo(System.currentTimeMillis() / 1000L, Offset.offset(2L));
        assertThat(result).isEqualTo(10L);
    }
}
