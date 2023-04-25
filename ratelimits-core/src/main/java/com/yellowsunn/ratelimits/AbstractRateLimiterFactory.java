package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;

public abstract class AbstractRateLimiterFactory implements RateLimiterFactory {
    protected abstract TokenBucketRepository createTokenBucketRepository();

    protected abstract RateLimiter createRateLimiter();
}
