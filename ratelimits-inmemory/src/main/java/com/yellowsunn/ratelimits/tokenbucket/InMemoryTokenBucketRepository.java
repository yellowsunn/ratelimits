package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.RateLimitRule;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.time.Clock;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

public class InMemoryTokenBucketRepository implements TokenBucketRepository {
    private final Map<String, Bucket> buckets;
    private final Clock clock;

    public InMemoryTokenBucketRepository(int maxKeySize) {
        this(maxKeySize, Clock.systemUTC());
    }

    public InMemoryTokenBucketRepository(int maxKeySize, Clock clock) {
        this.buckets = ExpiringMap.builder()
                .maxSize(maxKeySize)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .build();
        this.clock = clock;
    }

    @Override
    public Bucket findBucketByRule(String key, RateLimitRule rule) {
        requireNonNull(rule);
        if (key == null) {
            return null;
        }
        Bucket bucket = buckets.get(key);
        if (bucket == null || notEqual(rule, bucket.getRule())) {
            return null;
        }
        return bucket;
    }

    @Override
    public Bucket createBucketByRule(String key, RateLimitRule rule) {
        requireNonNull(key);
        requireNonNull(rule);
        buckets.put(key, new Bucket(rule, clock));
        return buckets.get(key);
    }

    @Override
    public void saveBucket(String key, Bucket bucket) {
        requireNonNull(key);
        requireNonNull(bucket);

        buckets.put(key, bucket);
    }

    @Override
    public boolean deleteBucket(String key) {
        requireNonNull(key);
        buckets.remove(key);
        return true;
    }
}
