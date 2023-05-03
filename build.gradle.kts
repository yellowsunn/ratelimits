plugins {
    id("java")
    `maven-publish`
}

group = "com.yellowsunn"
version = "1.0-SNAPSHOT"

allprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    dependencies {
        implementation("com.google.guava:guava:${Versions.guava}")
        implementation("org.apache.commons:commons-lang3:${Versions.apacheCommons}")
        implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
        testImplementation("ch.qos.logback:logback-classic:${Versions.logback}")
        testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
        testImplementation("org.assertj:assertj-core:${Versions.assertj}")
        testImplementation("org.mockito:mockito-all:${Versions.mockito}")
    }

    tasks.test {
        useJUnitPlatform()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }
}
