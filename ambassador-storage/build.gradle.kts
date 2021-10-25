import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
    id("jooq-conventions")
}

tasks.getByName("sourcesJar") {
    dependsOn("generateJooq")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    enabled = false
}

val jacksonVersion: String by extra
val postgresqlDriverVersion: String by extra
val testcontainersVersion: String by extra

dependencies {

    implementation(project(":ambassador-model"))
    api(project(":ambassador-commons"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:8.0.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    api("com.vladmihalcea:hibernate-types-52:2.13.0")
    implementation("org.jooq:jooq-meta-extensions-hibernate:3.15.3") {
        exclude("com.h2database", "h2")
    }
    runtimeOnly("org.postgresql:postgresql:$postgresqlDriverVersion")

    jooqGenerator("org.testcontainers:postgresql:$testcontainersVersion")
    jooqGenerator("org.postgresql:postgresql:$postgresqlDriverVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("net.ttddyy:datasource-proxy:1.7")
    testImplementation(project(":ambassador-fake-source"))
}

jooq {
    configuration {
        jdbc {
            username = "postgres"
            password = "postgres"
            driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
            url =
                "jdbc:tc:postgresql:13:///ambassador?TC_TMPFS=/testtmpfs:rw&amp;TC_INITFUNCTION=com.filipowm.ambassador.gradle.utils.DatabaseInit::flyway"
        }
        generator {
            target {
                packageName = "com.filipowm.ambassador.storage.jooq"
                directory = "build/generated-src/jooq/main"
            }
            database {
                inputSchema = "public"
            }
            generate {
                isImmutablePojos = true
                isPojosEqualsAndHashCode = true
                isFluentSetters = true
                isJavaTimeTypes = true
                isDeprecationOnUnknownTypes = false
            }
        }
    }
}

description = "ambassador-storage"
