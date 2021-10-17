plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    compileOnly("org.flywaydb:flyway-core:8.0.1")
}

description = "ambassador-commons"
