package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.time.DefaultTimeSupplier;
import com.yellowsunn.ratelimits.time.TimeSupplier;
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
    private final TimeSupplier timeSupplier;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public RedisTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository, RedissonClient redissonClient) {
        this(tokenBucketRepository, redissonClient, new DefaultTimeSupplier());
    }

    public RedisTokenBucketRateLimiter(TokenBucketRepository tokenBucketRepository,
                                       RedissonClient redissonClient,
                                       TimeSupplier timeSupplier) {
        this.tokenBucketRepository = tokenBucketRepository;
        this.redissonClient = redissonClient;
        this.timeSupplier = timeSupplier;
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
        Long tokenAmount = tokenBucketRepository.findTokenAmount(key);
        if (tokenAmount == null) {
            tokenBucketRepository.saveTokenAmount(key, rule.getCapacity() - 1);
            return true;
        }

        tokenAmount = refill(key, rule, tokenAmount);
        if (tokenAmount <= 0L) {
            return false;
        }

        return tokenBucketRepository.decrementTokenAmount(key);
    }

    @Override
    public boolean resetLimit(String key) {
        requireNonNull(key);
        return tokenBucketRepository.deleteKey(key);
    }

    private long refill(String key, RateLimitRule rule, long tokenAmount) {
        long refillAmount = refillAmount(key, rule);
        if (refillAmount > 0) {
            long newAmount = Math.min(tokenAmount + refillAmount, rule.getCapacity());
            tokenBucketRepository.saveTokenAmount(key, newAmount);
            return newAmount;
        }
        return tokenAmount;
    }

    private long refillAmount(String key, RateLimitRule rule) {
        long now = timeSupplier.now();
        Long lastModifiedTime = tokenBucketRepository.lastModifiedTime(key);
        if (lastModifiedTime == null || now < lastModifiedTime) {
            return rule.getCapacity();
        }

        double elapsedTime = (double) now - lastModifiedTime;
        return (long) (elapsedTime * rule.getCapacity() / rule.getDuration().getSeconds());
    }
}
