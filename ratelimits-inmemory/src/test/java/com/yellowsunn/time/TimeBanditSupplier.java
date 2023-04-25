package com.yellowsunn.time;

import com.yellowsunn.ratelimits.time.TimeSupplier;

import java.util.concurrent.atomic.AtomicLong;

public class TimeBanditSupplier implements TimeSupplier {

    private final AtomicLong time = new AtomicLong(10000000000L);

    public void addUnixTime(long millis) {
        time.addAndGet(millis);
    }

    @Override
    public long now() {
        return this.time.get() / 1000L;
    }
}
