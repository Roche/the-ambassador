import pl.filipowm.opensource.ambassador.gradle.jooq.JooqPlugin

apply<JooqPlugin>()

tasks.getByName("compileKotlin").dependsOn(tasks.getByName("generateJooq"))
tasks.getByName("compileJava").dependsOn(tasks.getByName("generateJooq"))
