package com.yellowsunn.ratelimits.extension;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisStandAloneRegisterExtension implements BeforeAllCallback, AfterAllCallback {
    private RedisClient redisClient;
    private RedissonClient redissonClient;
    private StatefulRedisConnection<String, String> connect;
    private RedisCommands<String, String> redisCommands;

    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis").withTag("6-alpine"))
                .withExposedPorts(REDIS_PORT);
        REDIS_CONTAINER.start();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        this.redissonClient = buildRedissonClient();
        this.redisClient = RedisClient.create(RedisURI.create(REDIS_HOST, REDIS_CONTAINER.getMappedPort(REDIS_PORT)));
        this.connect = redisClient.connect();
        this.redisCommands = connect.sync();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        connect.close();
        redisClient.close();
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public StatefulRedisConnection<String, String> getConnect() {
        return this.connect;
    }

    public RedisCommands<String, String> getRedisCommands() {
        return redisCommands;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    private RedissonClient buildRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_HOST + ":" + REDIS_CONTAINER.getMappedPort(REDIS_PORT));
        return Redisson.create(config);
    }
}
