package com.yellowsunn.ratelimits;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;

public class RateLimitServletExample extends HttpServlet {
    private final transient RateLimiter rateLimiter;
    private final transient RateLimitRule rateLimitRule;

    public RateLimitServletExample() {
        RateLimiterFactory rateLimiterFactory = new InMemoryRateLimiterFactory();
        this.rateLimiter = rateLimiterFactory.getInstance();

        this.rateLimitRule = new RateLimitRule(10, Duration.ofSeconds(1));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String key = request.getRequestURI();

        boolean isAcquired = rateLimiter.acquire(key, rateLimitRule);
        if (!isAcquired) {
            // 429: Too Many Requests
            response.setStatus(429);
            return;
        }
        response.setStatus(200);
    }
}
