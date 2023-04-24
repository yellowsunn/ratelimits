package com.yellowsunn.ratelimits.time;

import java.util.concurrent.atomic.AtomicLong;

public class TimeBanditSupplier implements TimeSupplier {

    private final AtomicLong time = new AtomicLong(10000000000L);

    public long addUnixTime(long millis) {
        return time.addAndGet(millis);
    }

    @Override
    public long now() {
        return this.time.get() / 1000L;
    }
}
