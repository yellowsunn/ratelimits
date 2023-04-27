package com.yellowsunn.ratelimits.tokenbucket;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTokenBucketRepositoryTest extends AbstractTokenBucketRepositoryTest {

    @BeforeEach
    void setUp() {
        tokenBucketRepository = new InMemoryTokenBucketRepository(10, clock);
    }
}
