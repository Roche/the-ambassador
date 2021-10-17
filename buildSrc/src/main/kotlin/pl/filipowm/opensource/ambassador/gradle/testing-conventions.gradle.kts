import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.gradle.kotlin.dsl.support.serviceOf
import pl.filipowm.opensource.ambassador.gradle.utils.TestResultLogger
import java.util.Optional.ofNullable

val junitVersion: String by extra
val testcontainersVersion: String by extra

plugins {
    id("java-conventions")
    id("idea")
}

idea {
    module {
        sourceDirs.remove(file("src/integrationTest/kotlin"))
        testSourceDirs.add(file("src/integrationTest/kotlin"))
    }
}

fun Test.configure() {
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

tasks.test {
    configure()
}

sourceSets {
    create("integrationTest") {
        compileClasspath += main.get().output + configurations.testRuntimeClasspath
        runtimeClasspath += output + compileClasspath
    }
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    configure()
}

tasks.check {
    dependsOn(integrationTest)
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
    extendsFrom(configurations.testImplementation.get())
}

val ci by tasks.registering {
    description = "Runs the tests on CI based on flag"
    group = "verification"
    val testType = ofNullable(System.getenv("TEST_TYPE"))
        .map(String::toUpperCase)
        .orElse("ALL")
    when(testType) {
        "INTEGRATION" -> dependsOn(integrationTest)
        "UNIT" -> dependsOn(tasks.test.get())
        else -> dependsOn(tasks.check.get())
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("com.devskiller:jfairy:0.6.4")
    testImplementation("com.tngtech.archunit:archunit-junit5:0.21.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    integrationTestImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}
