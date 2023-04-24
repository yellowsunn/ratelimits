plugins {
    id("java")
}

group = "com.yellowsunn"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "java-library")
}

subprojects {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
        testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
