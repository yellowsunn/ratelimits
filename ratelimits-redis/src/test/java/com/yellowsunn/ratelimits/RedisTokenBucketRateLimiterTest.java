package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.extension.RedisStandAloneRegisterExtension;
import com.yellowsunn.ratelimits.time.TimeBanditSupplier;
import com.yellowsunn.ratelimits.tokenbucket.RedisTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class RedisTokenBucketRateLimiterTest {
    @RegisterExtension
    static RedisStandAloneRegisterExtension extension = new RedisStandAloneRegisterExtension();

    TimeBanditSupplier timeSupplier;
    TokenBucketRepository tokenBucketRepository;
    RedisTokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        timeSupplier = new TimeBanditSupplier();
        tokenBucketRepository = new RedisTokenBucketRepository(extension.getRedisCommands(), timeSupplier);
        rateLimiter = new RedisTokenBucketRateLimiter(tokenBucketRepository, extension.getRedissonClient(), timeSupplier);
    }

    @Test
    void shouldHaveConcurrent10AcquireCount() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        String key = "test-key-" + UUID.randomUUID();

        RateLimitRule rateLimitRule = new RateLimitRule(10, Duration.ofSeconds(1L));

        AtomicLong acquireCount = new AtomicLong(0L);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    boolean isSuccess = rateLimiter.acquire(key, rateLimitRule);
                    System.out.println("isSuccess: " + isSuccess);
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
        String key = "test-key-" + UUID.randomUUID();

        IntStream.rangeClosed(1, 50).forEach(i -> {
            boolean isSuccess = rateLimiter.acquire(key, rateLimitRule);
            if (isSuccess) {
                acquireCount.incrementAndGet();
            }
            timeSupplier.addUnixTime(500);
        });

        assertThat(acquireCount.get()).isEqualTo(25L);
    }

    @Test
    void ShouldResetLimitToken() {
        String key = "test-key-" + UUID.randomUUID();

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
