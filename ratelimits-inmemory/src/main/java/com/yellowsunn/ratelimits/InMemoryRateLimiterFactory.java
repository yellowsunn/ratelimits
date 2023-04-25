package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;

public class InMemoryRateLimiterFactory extends AbstractRateLimiterFactory {
    private final int maxKeySize;
    private final RateLimiter rateLimiter;

    public InMemoryRateLimiterFactory() {
        this(10_000);
    }

    public InMemoryRateLimiterFactory(int maxKeySize) {
        this.maxKeySize = maxKeySize;
        this.rateLimiter = createRateLimiter();
    }

    @Override
    public RateLimiter getInstance() {
        return rateLimiter;
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    protected TokenBucketRepository createTokenBucketRepository() {
        return new InMemoryTokenBucketRepository(maxKeySize);
    }

    @Override
    protected RateLimiter createRateLimiter() {
        return new InMemoryTokenBucketRateLimiter(createTokenBucketRepository());
    }
}
