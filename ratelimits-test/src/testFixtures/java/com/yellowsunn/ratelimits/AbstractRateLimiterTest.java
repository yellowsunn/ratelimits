package com.yellowsunn.ratelimits;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public abstract class AbstractRateLimiterTest {
    RateLimiter rateLimiter;
    Clock clock = Mockito.mock(Clock.class);

    @Test
    void shouldHaveConcurrent10AcquireCount() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        String key = "ip:127.0.0.1_" + UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.now());

        RateLimitRule rateLimitRule = new RateLimitRule(10, Duration.ofSeconds(1L));

        AtomicLong acquireCount = new AtomicLong(0L);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    boolean isSuccess = rateLimiter.acquire(key, rateLimitRule);
                    if (isSuccess) {
                        acquireCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        assertThat(acquireCount.get()).isEqualTo(10L);
    }

    @Test
    void shouldRefillToken() {
        RateLimitRule rateLimitRule = new RateLimitRule(1, Duration.ofSeconds(1L));
        AtomicLong acquireCount = new AtomicLong(0L);
        String key = "ip:127.0.0.1_" + UUID.randomUUID();
        Instant mockInstant = Instant.ofEpochMilli(1000000);
        given(clock.instant()).willReturn(mockInstant);

        IntStream.rangeClosed(1, 50).forEach(i -> {
            boolean isSuccess = rateLimiter.acquire(key, rateLimitRule);
            if (isSuccess) {
                acquireCount.incrementAndGet();
            }
            given(clock.instant()).willReturn(mockInstant.plusMillis(i * 500L));
        });

        assertThat(acquireCount.get()).isEqualTo(25L);
    }

    @Test
    void shouldResetLimitToken() {
        String key = "ip:127.0.0.1_" + UUID.randomUUID();
        given(clock.instant()).willReturn(Instant.now());

        RateLimitRule rateLimitRule = new RateLimitRule(100L, Duration.ofSeconds(1));
        IntStream.rangeClosed(1, 100).forEach(i ->
                assertThat(rateLimiter.acquire(key, rateLimitRule)).isTrue()
        );

        assertThat(rateLimiter.resetLimit(key)).isTrue();

        IntStream.rangeClosed(1, 100).forEach(i ->
                assertThat(rateLimiter.acquire(key, rateLimitRule)).isTrue()
        );
    }
}
