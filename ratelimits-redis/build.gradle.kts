description = "RateLimit Redis"

dependencies {
    implementation(project(":ratelimits-core"))
    implementation("io.lettuce:lettuce-core:${Versions.lettuce}")
    implementation("org.redisson:redisson:${Versions.redisson}")
    testImplementation("org.testcontainers:testcontainers:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    testImplementation(testFixtures(project(":ratelimits-test")))
}
