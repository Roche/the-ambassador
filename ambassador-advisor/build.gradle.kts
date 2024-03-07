plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
}

val kotlinCoroutinesVersion: String by extra
val springApiVersion: String by extra
val springdocVersion: String by extra

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-commons"))
    implementation(project(":ambassador-storage"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.jknack:handlebars:4.4.0")
    implementation("com.github.jknack:handlebars-helpers:4.3.0")

    implementation("io.github.filipowm:spring-api-starter:$springApiVersion")

    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(project(":ambassador-fake-source"))

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