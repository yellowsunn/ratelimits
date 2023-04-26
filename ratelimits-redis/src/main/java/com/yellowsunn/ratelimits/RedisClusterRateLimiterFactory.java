package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.RedisTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import org.redisson.api.RedissonClient;

class RedisClusterRateLimiterFactory extends AbstractRateLimiterFactory {
    private final RedisClusterClient redisClusterClient;
    private final RedissonClient redissonClient;
    private final StatefulRedisClusterConnection<String, String> connect;

    private final RateLimiter rateLimiter;

    public RedisClusterRateLimiterFactory(RedisClusterClient redisClusterClient, RedissonClient redissonClient) {
        this.redisClusterClient = redisClusterClient;
        this.redissonClient = redissonClient;
        this.connect = redisClusterClient.connect();

        this.rateLimiter = createRateLimiter();
    }

    @Override
    public RateLimiter getInstance() {
        return rateLimiter;
    }

    @Override
    public void close() {
        connect.close();
        redisClusterClient.shutdown();
    }

    @Override
    protected TokenBucketRepository createTokenBucketRepository() {
        return new RedisTokenBucketRepository(connect.sync());
    }

    @Override
    protected RateLimiter createRateLimiter() {
        return new RedisTokenBucketRateLimiter(createTokenBucketRepository(), redissonClient);
    }
}
