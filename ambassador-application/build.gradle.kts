plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
}

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-storage"))
    implementation(project(":ambassador-gitlab"))
    implementation(project(":ambassador-document-analyzer"))
    implementation(project(":ambassador-commons"))
    implementation(project(":ambassador-fake-source"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.0")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    
}
//
//description = "ambassador-application"
