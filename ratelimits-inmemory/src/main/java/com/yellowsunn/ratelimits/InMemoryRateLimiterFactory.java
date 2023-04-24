package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;

import java.util.concurrent.TimeUnit;

public class InMemoryRateLimiterFactory implements RateLimiterFactory {
    private final int maxKeySize;
    private final long expireKeyDuration;
    private final TimeUnit timeUnit;

    public InMemoryRateLimiterFactory() {
        this(10_000, 1L, TimeUnit.SECONDS);
    }

    public InMemoryRateLimiterFactory(int maxKeySize, long expireKeyDuration, TimeUnit timeUnit) {
        this.maxKeySize = maxKeySize;
        this.expireKeyDuration = expireKeyDuration;
        this.timeUnit = timeUnit;
    }

    @Override
    public RateLimiter getInstance() {
        return new InMemoryTokenBucketRateLimiter(new InMemoryTokenBucketRepository(maxKeySize, expireKeyDuration, timeUnit));
    }
}
