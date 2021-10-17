plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val kotlinCoroutinesVersion: String by extra
val slf4jVersion: String by extra

dependencies {
    implementation(project(":ambassador-model"))
    implementation(project(":ambassador-commons"))
    api(project(":ambassador-gitlab-client"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

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
