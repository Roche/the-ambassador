plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation(project(":ambassador-model"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
//    implementation("com.github.pemistahl:lingua:1.0.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

description = "ambassador-document-analyzer"
