plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-document-analyzer"))
    implementation(project(":ambassador-commons"))
    api(project(":ambassador-gitlab-client"))
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
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
