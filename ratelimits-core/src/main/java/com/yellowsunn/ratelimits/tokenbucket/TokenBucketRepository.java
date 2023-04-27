package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;

public interface TokenBucketRepository {
    Bucket findBucket(String key);

    Bucket createBucketByRule(String key, RateLimitRule rule);

    void saveBucket(String key, Bucket bucket);

    boolean deleteBucket(String key);
}
