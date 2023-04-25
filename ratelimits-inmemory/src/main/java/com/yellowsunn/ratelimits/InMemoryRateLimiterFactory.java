package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;

public class InMemoryRateLimiterFactory implements RateLimiterFactory {
    private final int maxKeySize;

    public InMemoryRateLimiterFactory() {
        this(10_000);
    }

    public InMemoryRateLimiterFactory(int maxKeySize) {
        this.maxKeySize = maxKeySize;
    }

    @Override
    public RateLimiter getInstance() {
        return new InMemoryTokenBucketRateLimiter(new InMemoryTokenBucketRepository(maxKeySize));
    }
}
