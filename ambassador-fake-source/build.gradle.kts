plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")

    implementation(project(":ambassador-model"))
    api(project(":ambassador-commons"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")

}

description = "ambassador-fake-source"
