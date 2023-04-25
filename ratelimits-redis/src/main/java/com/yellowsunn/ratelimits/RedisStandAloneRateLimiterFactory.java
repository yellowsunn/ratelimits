package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.RedisTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisStandAloneRateLimiterFactory extends AbstractRateLimiterFactory {
    private final RedisClient redisClient;
    private final RedissonClient redissonClient;
    private final StatefulRedisConnection<String, String> connection;

    private final RateLimiter rateLimiter;

    public RedisStandAloneRateLimiterFactory() {
        this(RedisURI.create("127.0.0.1", 6379));
    }

    public RedisStandAloneRateLimiterFactory(RedisURI redisURI) {
        this.redisClient = RedisClient.create(redisURI);
        this.redissonClient = buildRedissonClient(redisURI);
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
        redisClient.close();
    }

    @Override
    protected TokenBucketRepository createTokenBucketRepository() {
        return new RedisTokenBucketRepository(connection.sync());
    }

    @Override
    protected RateLimiter createRateLimiter() {
        return new RedisTokenBucketRateLimiter(createTokenBucketRepository(), redissonClient);
    }

    private RedissonClient buildRedissonClient(RedisURI redisURI) {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("%s:%s", redisURI.getHost(), redisURI.getPort()));

        return Redisson.create(config);
    }
}
