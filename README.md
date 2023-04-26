# ratelimits
Java library for rate limiting. This library is implemented using the [token bucket algorithm]("https://en.wikipedia.org/wiki/Token_bucket").


## Setup
This library has been published on [JitPack]("https://jitpack.io/#yellowsunn/ratelimits/1.0.0").

Add it in your root build.gradle at the end of repositories.
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## How to use
### 1. In-memory Rate Limit

#### Add the dependency
```gradle
dependencies {
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-core:1.0.0'
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-inmemory:1.0.0'
}
```

#### Example
```java
// Rule that allows 10 requests per second.
RateLimitRule rule = new RateLimitRule(10, Duration.ofSeconds(1L));

RateLimiterFactory factory = new InMemoryRateLimiterFactory();
RateLimiter rateLimiter = factory.getInstance();

// Represents whether acquisition was successful.
boolean isAcquired = rateLimiter.acquire("ip:127.0.0.1", rule);
```
---
### 2. Redis Rate Limit
Rate limiting with Redis storage and distributed lock control with Redisson.

#### Add the dependency
```gradle
dependencies {
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-core:1.0.0'
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-redis:1.0.0'
}
```

#### Example
```java
// Rule that allows 10 requests per second.
RateLimitRule rule = new RateLimitRule(10, Duration.ofSeconds(1L));

// Redis Standalone server
RateLimiterFactory factory = new RedisRateLimiterFactory("127.0.0.1", 6379);
//// When using Redis cluster, pass RedisClusterClient and Redisson config as parameters.
// RateLimiterFactory factory = new RedisRateLimiterFactory(new RedisClusterClient(...), new Config(...));

RateLimiter rateLimiter = factory.getInstance();

// Represents whether acquisition was successful.
boolean isAcquired = rateLimiter.acquire("ip:127.0.0.1", rule);
```
