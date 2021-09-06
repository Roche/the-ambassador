package pl.filipowm.opensource.ambassador.gradle.jooq

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.GradleVersion

class JooqPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        // abort if old Gradle version is not supported
        check(GradleVersion.current().baseVersion >= GradleVersion.version("6.1")) { "This version of the jooq plugin is not compatible with Gradle < 6.1" }

        // apply Java base plugin, making it possible to also use the jOOQ plugin for Android builds
        project.plugins.apply(JavaBasePlugin::class.java)

        // add jOOQ DSL extension
        val extension = project.extensions.create("jooq", JooqExtension::class.java)

        // create configuration for the runtime classpath of the jooq code generator (shared by all jooq configuration domain objects)
        val runtimeConfiguration: Configuration = createJooqGeneratorRuntimeConfiguration(project)

        val jooq: TaskProvider<JooqGenerate> = project.tasks.register("generateJooq", JooqGenerate::class.java, extension.configuration, runtimeConfiguration)
        jooq.configure {
            this.setDescription(String.format("Generates the jOOQ sources from the jOOQ configuration."))
            this.setGroup("jOOQ")
        }
        val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

        sourceSets.configureEach {
            val outputProvider = jooq.flatMap { it.getOutputDir() }
            this.java.srcDir(outputProvider)
        }
    }

    companion object {

        private val dependencies = listOf(
            "org.jooq:jooq-codegen",
//            "javax.xml.bind:jaxb-api:2.3.1",
//            "org.glassfish.jaxb:jaxb-core:2.3.0.1",
//            "org.glassfish.jaxb:jaxb-runtime:2.3.3",
//            "javax.activation:activation:1.1.1"
        )

        /**
         * Adds the configuration that holds the classpath to use for invoking jOOQ. Users can add their JDBC driver and any generator extensions they might have. Explicitly add JAXB
         * dependencies since they have been removed from JDK 9 and higher. Explicitly add Activation dependency since it has been removed from JDK 11 and higher.
         */
        private fun createJooqGeneratorRuntimeConfiguration(project: Project): Configuration {
            val jooqGeneratorRuntime: Configuration = project.configurations.create("jooqGenerator")
            jooqGeneratorRuntime.setDescription(
                "The classpath used to invoke the jOOQ code generator. Add your JDBC driver, generator extensions, and additional dependencies here."
            )
            dependencies.forEach { project.dependencies.add(jooqGeneratorRuntime.name, it) }
            return jooqGeneratorRuntime
        }
    }

}