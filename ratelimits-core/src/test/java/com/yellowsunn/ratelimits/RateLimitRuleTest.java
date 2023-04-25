package com.yellowsunn.ratelimits;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RateLimitRuleTest {
    @Test
    void shouldHaveValidObject() {
        RateLimitRule rateLimitRule = new RateLimitRule(10, Duration.ofSeconds(1));

        assertThat(rateLimitRule).isNotNull();
    }

    @Test
    void shouldHaveCapacityGoeZero() {
        long capacity = -1L;

        Throwable throwable = catchThrowable(() -> new RateLimitRule(capacity, Duration.ofSeconds(1L)));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHaveDurationGoe1Second() {
        Duration duration = Duration.ofMillis(999);

        Throwable throwable = catchThrowable(() -> new RateLimitRule(10, duration));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }
}
