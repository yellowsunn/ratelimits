package com.yellowsunn.ratelimits;

import java.io.Closeable;

public interface RateLimiterFactory extends Closeable {
    RateLimiter getInstance();
}
