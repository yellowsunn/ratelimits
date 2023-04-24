package com.yellowsunn.ratelimits.tokenbucket;

public interface TokenBucketRepository {
    boolean saveTokenAmount(String key, long amount);

    Long findTokenAmount(String key);

    Long lastModifiedTime(String key);

    boolean decrementTokenAmount(String key);

    boolean deleteKey(String key);
}
