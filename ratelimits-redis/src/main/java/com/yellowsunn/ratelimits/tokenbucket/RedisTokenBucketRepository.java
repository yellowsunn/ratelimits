package com.yellowsunn.ratelimits.tokenbucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yellowsunn.ratelimits.RateLimitRule;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;

public class RedisTokenBucketRepository implements TokenBucketRepository {
    private final RedisClusterCommands<String, String> redisCommands;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    public RedisTokenBucketRepository(RedisClusterCommands<String, String> redisCommands) {
        this(redisCommands, Clock.systemUTC());
    }

    public RedisTokenBucketRepository(RedisClusterCommands<String, String> redisCommands, Clock clock) {
        this.redisCommands = redisCommands;
        this.clock = clock;
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public Bucket findBucket(String key) {
        return getRedisValue(key);
    }

    @Override
    public Bucket createBucketByRule(String key, RateLimitRule rule) {
        Bucket bucket = new Bucket(rule, clock);
        boolean isSuccess = setRedisValue(key, bucket);
        if (isSuccess) {
            return bucket;
        }
        return null;
    }

    @Override
    public void saveBucket(String key, Bucket bucket) {
        setRedisValue(key, bucket);
    }

    @Override
    public boolean deleteBucket(String key) {
        return redisCommands.del(key) != null;
    }

    private boolean setRedisValue(String key, Bucket value) {
        try {
            String bucketValue = objectMapper.writeValueAsString(value);
            String result = redisCommands.set(key, bucketValue);
            return StringUtils.equalsIgnoreCase(result, "OK");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set redis value.", e);
        }
    }

    private Bucket getRedisValue(String key) {
        String bucketValue = redisCommands.get(key);
        if (bucketValue == null) {
            return null;
        }
        try {
            RedisBucket redisBucket = objectMapper.readValue(bucketValue, RedisBucket.class);
            redisBucket.changeClock(clock);
            return redisBucket;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize RedisBucket.class", e);
        }
    }
}
