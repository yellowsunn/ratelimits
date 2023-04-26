package com.yellowsunn.ratelimits;

import com.yellowsunn.ratelimits.time.TimeBanditSupplier;
import com.yellowsunn.ratelimits.tokenbucket.InMemoryTokenBucketRepository;
import com.yellowsunn.ratelimits.tokenbucket.TokenBucketRepository;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTokenBucketRateLimiterTest extends AbstractRateLimiterTest {
    TokenBucketRepository tokenBucketRepository;

    @BeforeEach
    void setUp() {
        super.timeSupplier = new TimeBanditSupplier();
        tokenBucketRepository = new InMemoryTokenBucketRepository(10, timeSupplier);
        super.rateLimiter = new InMemoryTokenBucketRateLimiter(tokenBucketRepository, timeSupplier);
    }
}
