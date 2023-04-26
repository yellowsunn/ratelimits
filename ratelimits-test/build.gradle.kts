description = "RateLimit Test"

plugins {
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation(project(":ratelimits-core"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testFixturesImplementation("org.assertj:assertj-core:${Versions.assertj}")
}
