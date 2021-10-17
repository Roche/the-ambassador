plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val slf4jVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
}

description = "ambassador-commons"
