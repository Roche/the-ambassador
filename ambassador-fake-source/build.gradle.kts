plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    enabled = false
}

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")

    implementation(project(":ambassador-model"))
    api(project(":ambassador-commons"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")

}

description = "ambassador-fake-source"
