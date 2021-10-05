plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation(project(":ambassador-commons"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.3")
    testImplementation("org.apache.commons:commons-math3:3.6.1")
}

description = "ambassador-model"
