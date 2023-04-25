package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import com.yellowsunn.time.TimeBanditSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTokenBucketRateLimiterTest {
    TimeBanditSupplier timeSupplier;
    TokenBucketRepository tokenBucketRepository;
    InMemoryTokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        timeSupplier = new TimeBanditSupplier();
        tokenBucketRepository = new InMemoryTokenBucketRepository(10, timeSupplier);
        rateLimiter = new InMemoryTokenBucketRateLimiter(tokenBucketRepository, timeSupplier);
    }

    @Test
    void shouldHaveConcurrent10AcquireCount() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        RateLimitRule rateLimitRule = new RateLimitRule(10, Duration.ofSeconds(1L));

        AtomicLong acquireCount = new AtomicLong(0L);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    boolean isSuccess = rateLimiter.acquire("test-key", rateLimitRule);
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

        IntStream.rangeClosed(1, 50).forEach(i -> {
            boolean isSuccess = rateLimiter.acquire("test-key", rateLimitRule);
            if (isSuccess) {
                acquireCount.incrementAndGet();
            }
            timeSupplier.addUnixTime(500);
        });

        assertThat(acquireCount.get()).isEqualTo(25L);
    }

    @Test
    void ShouldResetLimitToken() {
        RateLimitRule rateLimitRule = new RateLimitRule(100L, Duration.ofSeconds(1));
        IntStream.rangeClosed(1, 100).forEach(i ->
                assertThat(rateLimiter.acquire("test-key", rateLimitRule)).isTrue()
        );

        assertThat(rateLimiter.resetLimit("test-key")).isTrue();

        IntStream.rangeClosed(1, 100).forEach(i ->
                assertThat(rateLimiter.acquire("test-key", rateLimitRule)).isTrue()
        );
    }
}
