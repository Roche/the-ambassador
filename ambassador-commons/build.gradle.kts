plugins {
    id("kotlin-conventions")
    id("testing-conventions")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    compileOnly("org.flywaydb:flyway-core:7.7.3")
}

description = "ambassador-commons"
