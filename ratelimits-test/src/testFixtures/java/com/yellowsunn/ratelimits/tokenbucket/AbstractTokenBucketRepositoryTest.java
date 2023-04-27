package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public abstract class AbstractTokenBucketRepositoryTest {
    TokenBucketRepository tokenBucketRepository;
    Clock clock = mock(Clock.class);

    @Test
    void shouldHave9TokenAmount() {
        // given
        String key = "ip:127.0.0.1";
        RateLimitRule rule = new RateLimitRule(10, Duration.ofSeconds(1));
        given(clock.instant()).willReturn(Instant.now());

        tokenBucketRepository.createBucketByRule(key, rule);

        // when
        Bucket bucket = tokenBucketRepository.findBucketByRule(key, rule);
        bucket.tryAcquireToken();

        // then
        assertThat(bucket.getAmount()).isEqualTo(9L);
        assertThat(bucket.getLastRefillTime()).isCloseTo(Instant.now().getEpochSecond(), Offset.offset(2L));
    }

    @Test
    void shouldHaveZeroTokenAmount() {
        String key = "ip:127.0.0.1";
        RateLimitRule rule = new RateLimitRule(2, Duration.ofSeconds(1));
        given(clock.instant()).willReturn(Instant.now());

        tokenBucketRepository.createBucketByRule(key, rule);

        Bucket bucket = tokenBucketRepository.findBucketByRule(key, rule);
        assertThat(bucket.tryAcquireToken()).isTrue();
        assertThat(bucket.tryAcquireToken()).isTrue();
        assertThat(bucket.tryAcquireToken()).isFalse();

        assertThat(bucket.getAmount()).isZero();
    }

    @Test
    void shouldRefillTokenEachSecond() {
        String key = "ip:127.0.0.1";
        RateLimitRule rule = new RateLimitRule(1, Duration.ofSeconds(1));
        Instant mockInstant = Instant.ofEpochMilli(1000000);
        given(clock.instant()).willReturn(mockInstant);
        tokenBucketRepository.createBucketByRule(key, rule);

        Bucket bucket = tokenBucketRepository.findBucketByRule(key, rule);
        assertThat(bucket.tryAcquireToken()).isTrue();

        given(clock.instant()).willReturn(mockInstant.plusMillis(500));
        assertThat(bucket.tryAcquireToken()).isFalse();

        given(clock.instant()).willReturn(mockInstant.plusSeconds(1L));
        assertThat(bucket.tryAcquireToken()).isTrue();
    }
}
