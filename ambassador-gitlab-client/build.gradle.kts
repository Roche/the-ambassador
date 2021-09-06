plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation("io.ktor:ktor-client-core:1.5.4")
    implementation("io.ktor:ktor-client-cio:1.5.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("io.ktor:ktor-client-jackson:1.5.4")
    implementation("io.ktor:ktor-client-logging:1.5.4")
    implementation("io.ktor:ktor-client-auth:1.5.4")
    implementation("io.ktor:ktor-client-auth:1.5.4")
    implementation("io.ktor:ktor-client-auth:1.5.4")// https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4")// https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jdk8
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.4")

}

description = "ambassador-gitlab-client"

//dependencies {
//    api(platform(project(":ambassador-platform")))
//
////    implementation("io.ktor:ktor-client-core:1.6.2")
////    implementation("io.ktor:ktor-client-cio:1.6.2")
//    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.1")
//    testImplementation("org.testcontainers:junit-jupiter")
//}
