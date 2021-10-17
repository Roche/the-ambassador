plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val slf4jVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":ambassador-model"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
//    implementation("com.github.pemistahl:lingua:1.0.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

description = "ambassador-document-analyzer"
