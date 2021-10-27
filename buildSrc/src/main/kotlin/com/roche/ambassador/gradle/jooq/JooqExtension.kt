package com.roche.ambassador.gradle.jooq

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.closureOf
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import javax.inject.Inject

abstract class JooqExtension @Inject constructor(val project: Project) {
    val configuration: Property<Configuration> = project.objects.property(Configuration::class.java)
    fun configuration(c: Configuration.() -> Unit): Configuration {
        val configurationClosure = closureOf(c)
        val configuration = defaultConfiguration()
        project.configure(configuration, configurationClosure)
        this.configuration.set(configuration)
        return configuration
    }

    fun Configuration.jdbc(configure: Action<Jdbc>): Unit = configure.execute(this.jdbc)

    fun Configuration.generator(configure: Action<Generator>): Unit = configure.execute(this.generator)

    fun Generator.database(configure: Action<Database>): Unit = configure.execute(this.database)

    fun Generator.generate(configure: Action<Generate>): Unit = configure.execute(this.generate)

    fun Generator.target(configure: Action<Target>): Unit = configure.execute(this.target)

    private fun defaultConfiguration(): Configuration {
        return Configuration()
            .withLogging(Logging.INFO)
            .withGenerator(
                Generator()
                    .withTarget(
                        Target()
                            .withDirectory("build/generated-src/jooq/main")
                            .withClean(true)
                    )
                    .withDatabase(
                        Database().withIncludeRoutines(false)
                            .withIncludePackages(false)
                            .withIncludeSequences(false)
                            .withIncludeSystemTables(false)
                            .withIncludeInvisibleColumns(false)
                    )
                    .withGenerate(Generate())
            )
            .withJdbc(Jdbc())
    }
}