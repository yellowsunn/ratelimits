description = "RateLimit Inmemory"

dependencies {
    implementation(project(":ratelimits-core"))
    implementation("net.jodah:expiringmap:${Versions.expiringMap}")
    testImplementation(testFixtures(project(":ratelimits-test")))
}
