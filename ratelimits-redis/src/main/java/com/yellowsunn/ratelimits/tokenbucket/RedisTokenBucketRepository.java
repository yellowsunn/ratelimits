package com.yellowsunn.ratelimits.tokenbucket;

import com.yellowsunn.ratelimits.time.DefaultTimeSupplier;
import com.yellowsunn.ratelimits.time.TimeSupplier;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.apache.commons.lang3.StringUtils;

public class RedisTokenBucketRepository implements TokenBucketRepository {
    private final RedisClusterCommands<String, String> redisCommands;
    private final TimeSupplier timeSupplier;

    public RedisTokenBucketRepository(RedisClusterCommands<String, String> redisCommands) {
        this(redisCommands, new DefaultTimeSupplier());
    }

    public RedisTokenBucketRepository(RedisClusterCommands<String, String> redisCommands, TimeSupplier timeSupplier) {
        this.redisCommands = redisCommands;
        this.timeSupplier = timeSupplier;
    }

    @Override
    public boolean saveTokenAmount(String key, long amount) {
        boolean isSuccess = setValue(key, amount);
        isSuccess = setValue(getLastModifiedTimeKey(key), timeSupplier.now()) && isSuccess;
        return isSuccess;
    }

    @Override
    public Long findTokenAmount(String key) {
        return getValue(key);
    }

    @Override
    public Long lastModifiedTime(String key) {
        return getValue(getLastModifiedTimeKey(key));
    }

    @Override
    public boolean decrementTokenAmount(String key) {
        Long amount = getValue(key);
        if (amount == null) {
            return false;
        }
        return setValue(key, amount - 1);
    }

    @Override
    public boolean deleteKey(String key) {
        return redisCommands.del(key) != null;
    }

    private String getLastModifiedTimeKey(String key) {
        return String.format("%s_lastModifiedTime", key);
    }

    private boolean setValue(String key, long value) {
        String result = redisCommands.set(key, String.valueOf(value));
        return StringUtils.equalsIgnoreCase(result, "OK");
    }

    private Long getValue(String key) {
        String value = redisCommands.get(key);
        if (value == null) {
            return null;
        }
        return Long.valueOf(value);
    }
}
