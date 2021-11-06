@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("java-conventions")
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

val jvmTargetVersion: String by rootProject.extra
val detektVersion: String by extra
val kotlinVersion: String by extra

val applyKotlinOptions: (KotlinJvmOptions) -> Unit = {
    @Suppress("SpellCheckingInspection")
    it.freeCompilerArgs = listOf("-Xjsr305=strict")
    it.allWarningsAsErrors = false
    it.jdkHome = javaToolchains.compilerFor(java.toolchain).get().metadata.installationPath.asFile.absolutePath
    it.jvmTarget = jvmTargetVersion
    it.languageVersion = kotlinVersion
    it.apiVersion = kotlinVersion
}

tasks.compileKotlin {
    logger.info("Configuring KotlinCompile {} in project {}...", name, project.name)
    kotlinOptions {
        applyKotlinOptions(this)
    }
}

tasks.compileTestKotlin {
    logger.info("Configuring KotlinTestCompile {} in project {}...", name, project.name)
    kotlinOptions {
        applyKotlinOptions(this)
    }
}

detekt {
    ignoreFailures = true
    buildUponDefaultConfig = true
    config = files("$rootDir/detekt.yml")

    autoCorrect = true
    reports {
        xml.enabled = true
        html.enabled = false
        txt.enabled = false
        sarif.enabled = true
    }
    parallel = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = jvmTargetVersion
    this.onlyIf { project.hasProperty("runDetect") }
}

repositories {
    // Fix for https://github.com/detekt/detekt/issues/3712
    // TODO: https://github.com/mrclrchtr/gradle-kotlin-spring/issues/9 Remove it when the issue was closed
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}
