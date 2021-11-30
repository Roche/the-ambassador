@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.jvm.toolchain.internal.CurrentJvmToolchainSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("java-conventions")
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

val jdkVersion: String by rootProject.extra
val detektVersion: String by extra
val kotlinVersion: String by extra

val applyKotlinOptions: (KotlinJvmOptions) -> Unit = {
    @Suppress("SpellCheckingInspection")
    it.freeCompilerArgs = listOf("-Xjsr305=strict")
    it.allWarningsAsErrors = false
    it.languageVersion = kotlinVersion
    it.apiVersion = kotlinVersion
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(jdkVersion))
    }
}

tasks.compileKotlin {
    kotlinOptions {
        applyKotlinOptions(this)
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        applyKotlinOptions(this)
    }
}

detekt {
    ignoreFailures = true
    buildUponDefaultConfig = true
    config = files("$rootDir/detekt.yml")

    autoCorrect = true
    parallel = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = jdkVersion
    reports {
        xml.required.set(true)
        html.required.set(false)
        txt.required.set(false)
        sarif.required.set(true)
    }
    this.onlyIf { project.hasProperty("runDetekt") }
}

repositories {
    // Fix for https://github.com/detekt/detekt/issues/3712
    // TODO: https://github.com/mrclrchtr/gradle-kotlin-spring/issues/9 Remove it when the issue was closed
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}
