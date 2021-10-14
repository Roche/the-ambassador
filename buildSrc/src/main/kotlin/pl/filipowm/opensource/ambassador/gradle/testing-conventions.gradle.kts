import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED

val junitVersion: String by extra

plugins {
    id("java-conventions")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events = setOf(FAILED)
        exceptionFormat = FULL
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.devskiller:jfairy:0.6.4")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.21.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
