package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.Bucket;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class RedisTokenBucketRateLimiter implements RateLimiter {
    private final RedissonClient redissonClient;
    private final TokenBucketRepository tokenBucketRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public RedisTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository,
                                       RedissonClient redissonClient) {
        this.tokenBucketRepository = tokenBucketRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean acquire(String key, RateLimitRule rule) {
        requireNonNull(key);
        requireNonNull(rule);
        RLock lock = redissonClient.getLock(key + "-lock");
        try {
            boolean isAvailable = lock.tryLock(5L, 3L, TimeUnit.SECONDS);
            if (!isAvailable) {
                log.error("Timed out acquiring lock.");
                return false;
            }
            return acquireToken(key, rule);
        } catch (InterruptedException e) {
            log.error("Failed to acquire lock.", e);
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean acquireToken(String key, RateLimitRule rule) {
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

    @Override
    public boolean resetLimit(String key) {
        requireNonNull(key);
        return tokenBucketRepository.deleteBucket(key);
    }
}
