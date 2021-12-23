plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
}

val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-commons"))
    implementation(project(":ambassador-storage"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.jknack:handlebars:4.3.0")
    implementation("com.github.jknack:handlebars-helpers:4.3.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    enabled = false
}

description = "Advisor"