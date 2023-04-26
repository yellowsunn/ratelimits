package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.RedisTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.redisson.api.RedissonClient;

class RedisStandAloneRateLimiterFactory extends AbstractRateLimiterFactory {
    private final RedisClient redisClient;
    private final RedissonClient redissonClient;
    private final StatefulRedisConnection<String, String> connection;

    private final RateLimiter rateLimiter;

    public RedisStandAloneRateLimiterFactory(RedisClient redisClient, RedissonClient redissonClient) {
        this.redisClient = redisClient;
        this.redissonClient = redissonClient;
        this.connection = redisClient.connect();

        this.rateLimiter = createRateLimiter();
    }

    @Override
    public RateLimiter getInstance() {
        return rateLimiter;
    }

    @Override
    public void close() {
        connection.close();
        redisClient.shutdown();
    }

    @Override
    protected TokenBucketRepository createTokenBucketRepository() {
        return new RedisTokenBucketRepository(connection.sync());
    }

    @Override
    protected RateLimiter createRateLimiter() {
        return new RedisTokenBucketRateLimiter(createTokenBucketRepository(), redissonClient);
    }
}
