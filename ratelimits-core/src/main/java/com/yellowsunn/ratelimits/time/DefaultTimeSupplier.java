package com.yellowsunn.ratelimits.time;

public class DefaultTimeSupplier implements TimeSupplier {
    @Override
    public long now() {
        return System.currentTimeMillis() / 1000L;
    }
}
