package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.time.DefaultTimeSupplier;
import com.yellowsunn.ratelimits.time.TimeSupplier;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InMemoryTokenBucketRepository implements TokenBucketRepository {
    private final Map<String, Long> tokenBuckets;
    private final Map<String, Long> lastModifiedTimes;
    private final TimeSupplier timeSupplier;

    public InMemoryTokenBucketRepository(int maxKeySize, long expireKeyDuration, TimeUnit timeUnit) {
        this(maxKeySize, expireKeyDuration, timeUnit, new DefaultTimeSupplier());
    }

    public InMemoryTokenBucketRepository(int maxKeySize, long expireKeyDuration, TimeUnit timeUnit, TimeSupplier timeSupplier) {
        this.tokenBuckets = buildExpiringMap(maxKeySize, expireKeyDuration, timeUnit);
        this.lastModifiedTimes = buildExpiringMap(maxKeySize, expireKeyDuration, timeUnit);
        this.timeSupplier = timeSupplier;
    }

    @Override
    public boolean saveTokenAmount(String key, long amount) {
        tokenBuckets.put(key, amount);
        lastModifiedTimes.put(key, timeSupplier.now());
        return true;
    }

    @Override
    public Long findTokenAmount(String key) {
        return tokenBuckets.get(key);
    }

    @Override
    public Long lastModifiedTime(String key) {
        return lastModifiedTimes.get(key);
    }

    @Override
    public boolean decrementTokenAmount(String key) {
        Long amount = tokenBuckets.get(key);
        if (amount == null) {
            return false;
        }
        return saveTokenAmount(key, amount - 1);
    }

    @Override
    public boolean deleteKey(String key) {
        tokenBuckets.remove(key);
        lastModifiedTimes.remove(key);
        return true;
    }

    private Map<String, Long> buildExpiringMap(int maxKeySize, long expireKeyDuration, TimeUnit timeUnit) {
        return ExpiringMap.builder()
                .maxSize(maxKeySize)
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expiration(expireKeyDuration, timeUnit)
                .build();
    }
}