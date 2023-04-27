package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenBucketRepositoryTest {

    TokenBucketRepository tokenBucketRepository = new InMemoryTokenBucketRepository(10);

    @Test
    void ShouldHave9TokenAmount() {
        // given
        String key = "ip:127.0.0.1";
        RateLimitRule rule = new RateLimitRule(10, Duration.ofSeconds(1));
        tokenBucketRepository.createBucketByRule(key, rule);

        // when
        Bucket bucket = tokenBucketRepository.findBucket(key);
        bucket.tryAcquireToken();

        // then
        assertThat(bucket.getAmount()).isEqualTo(9L);
        assertThat(bucket.getLastRefillTime()).isCloseTo(Instant.now().getEpochSecond(), Offset.offset(2L));
    }
}
