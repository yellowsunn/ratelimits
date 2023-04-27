package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;

public interface TokenBucketRepository {
    Bucket findBucketByRule(String key, RateLimitRule rule);

    Bucket createBucketByRule(String key, RateLimitRule rule);

    void saveBucket(String key, Bucket bucket);

    boolean deleteBucket(String key);
}
