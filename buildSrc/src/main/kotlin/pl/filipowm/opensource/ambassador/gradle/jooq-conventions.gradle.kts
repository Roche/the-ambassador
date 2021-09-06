import com.filipowm.ambassador.gradle.jooq.JooqPlugin

apply<JooqPlugin>()

tasks.getByName("compileKotlin").dependsOn(tasks.getByName("generateJooq"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("generateJooq"))
