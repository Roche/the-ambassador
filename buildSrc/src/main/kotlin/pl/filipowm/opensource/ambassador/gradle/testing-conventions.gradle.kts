import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.kotlin.dsl.support.serviceOf
import pl.filipowm.opensource.ambassador.gradle.utils.TestResultLogger

val junitVersion: String by extra

plugins {
    id("java-conventions")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 2
    testLogging {
        events = setOf(PASSED, FAILED, SKIPPED)
        debug.events = setOf(STARTED, STANDARD_OUT)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    addTestListener(TestResultLogger(serviceOf()))
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.devskiller:jfairy:0.6.4")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.21.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
