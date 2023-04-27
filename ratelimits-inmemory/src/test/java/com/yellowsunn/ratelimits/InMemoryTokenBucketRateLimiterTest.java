package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTokenBucketRateLimiterTest extends AbstractRateLimiterTest {
    TokenBucketRepository tokenBucketRepository;

    @BeforeEach
    void setUp() {
        tokenBucketRepository = new InMemoryTokenBucketRepository(10, super.clock);
        super.rateLimiter = new InMemoryTokenBucketRateLimiter(tokenBucketRepository);
    }
}
