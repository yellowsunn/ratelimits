package com.yellowsunn.ratelimits.testcontainer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class RedisTestContainer {
    protected static final GenericContainer<?> REDIS_CONTAINER;
    protected static final String REDIS_HOST = "127.0.0.1";
    protected static final int REDIS_PORT = 6379;

    static {
        REDIS_CONTAINER = new GenericContainer(DockerImageName.parse("redis").withTag("6-alpine"))
                .withExposedPorts(REDIS_PORT);
        REDIS_CONTAINER.start();
    }

    protected static int getRealRedisPort() {
        return REDIS_CONTAINER.getMappedPort(REDIS_PORT);
    }
}
