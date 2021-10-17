plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

val ktorVersion : String by extra
val resilience4jVersion : String by extra
val slf4jVersion : String by extra
val jacksonVersion : String by extra
val kotlinCoroutinesVersion : String by extra

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-retry:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-kotlin:$resilience4jVersion")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")

}

description = "ambassador-gitlab-client"
