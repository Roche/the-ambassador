plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val slf4jVersion: String by extra
val kotlinCoroutinesVersion: String by extra
val jacksonVersion: String by extra

dependencies {
    implementation(project(":ambassador-commons"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("org.apache.commons:commons-math3:3.6.1")
}

description = "ambassador-model"
