import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("kotlin-conventions")
    id("testing-conventions")
    id("spring-conventions")
    id("jooq-conventions")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

dependencies {

    implementation(project(":ambassador-model"))
    api(project(":ambassador-commons"))
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:7.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    api("com.vladmihalcea:hibernate-types-52:2.10.4")
    implementation("org.jooq:jooq-meta-extensions-hibernate:3.14.8")
    runtimeOnly("org.postgresql:postgresql:42.2.22")

    jooqGenerator("org.testcontainers:postgresql:1.15.2")
    jooqGenerator("org.postgresql:postgresql:42.2.22")
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
