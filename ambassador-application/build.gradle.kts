import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

// FIXME disabled temporarily kapt to not process spring configuration, because it misses some properties and extends build time two times

plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
//    kotlin("kapt")
}

val springdocVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-storage"))
    implementation(project(":ambassador-gitlab"))
    implementation(project(":ambassador-document-analyzer"))
    implementation(project(":ambassador-commons"))
    implementation(project(":ambassador-fake-source"))
    implementation(project(":ambassador-advisor"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
//    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

//kapt {
//    annotationProcessor("org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor")
//}

tasks.getByName<BootBuildImage>("bootBuildImage") {
    builder = "paketobuildpacks/builder"
    imageName = "ghcr.io/roche/ambassador-indexer"
    environment = mapOf(
        "BP_OCI_TITLE" to "The Ambassador",
        "BP_OCI_AUTHORS" to "Mateusz Filipowicz",
        "BP_OCI_URL" to "https://github.com/Roche/the-ambassador",
        "BP_OCI_LICENSES" to "Apache-2.0"
    )
}

description = "The Ambassador"
