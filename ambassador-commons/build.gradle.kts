plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val slf4jVersion: String by extra
val kotlinCoroutinesVersion: String by extra
val jacksonVersion: String by extra

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("com.vladsch.flexmark:flexmark-all:0.62.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
}

description = "ambassador-commons"
