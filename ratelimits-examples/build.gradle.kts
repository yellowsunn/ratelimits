description = "RateLimit Examples"

dependencies {
    compileOnly(project(":ratelimits-core"))
    compileOnly(project(":ratelimits-inmemory"))
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
}
