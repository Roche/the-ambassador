plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-document-analyzer"))
    implementation(project(":ambassador-commons"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    implementation("io.ktor:ktor-client-core:1.6.2")
//    implementation("io.ktor:ktor-client-cio:1.6.2")
//    implementation("io.ktor:ktor-client-jackson:1.6.2")
//    implementation("io.ktor:ktor-client-logging:1.6.2")
//    implementation("io.ktor:ktor-client-auth:1.6.2")
    api("org.gitlab4j:gitlab4j-api:4.17.0")
//    implementation("org.springframework.retry:spring-retry:1.3.1")
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions {
//        freeCompilerArgs = listOf("-Xjsr305=strict")
//        jvmTarget = "11"
//    }
//}
//
//description = "ambassador-gitlab"
