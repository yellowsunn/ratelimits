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
        implementation("com.google.guava:guava:${Versions.guava}")
        implementation("org.apache.commons:commons-lang3:${Versions.apacheCommons}")
        implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
        testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
        testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
