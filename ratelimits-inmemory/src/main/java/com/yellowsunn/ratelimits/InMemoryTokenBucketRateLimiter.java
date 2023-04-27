package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.Bucket;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;

import static java.util.Objects.requireNonNull;

public class InMemoryTokenBucketRateLimiter implements RateLimiter {
    private final TokenBucketRepository tokenBucketRepository;

    public InMemoryTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository) {
        this.tokenBucketRepository = tokenBucketRepository;
    }

    @Override
    public boolean acquire(String key, RateLimitRule rule) {
        requireNonNull(key);
        requireNonNull(rule);

        synchronized (this) {
            Bucket bucket = tokenBucketRepository.findBucket(key);
            if (bucket == null) {
                bucket = tokenBucketRepository.createBucketByRule(key, rule);
            }

            boolean isAcquired = bucket.tryAcquireToken();
            if (isAcquired) {
                tokenBucketRepository.saveBucket(key, bucket);
            }
            return isAcquired;
        }
    }

    @Override
    public boolean resetLimit(String key) {
        requireNonNull(key);
        return tokenBucketRepository.deleteBucket(key);
    }
}
