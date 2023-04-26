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

Add the dependency
```gradle
dependencies {
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-core:1.0.0'
    implementation 'com.github.yellowsunn.ratelimits:ratelimits-inmemory:1.0.0'
}
```

Example
```java
RateLimiterFactory factory = new InMemoryRateLimiterFactory();
RateLimiter rateLimiter = factory.getInstance();

RateLimitRule rule = new RateLimitRule(10, );
```
