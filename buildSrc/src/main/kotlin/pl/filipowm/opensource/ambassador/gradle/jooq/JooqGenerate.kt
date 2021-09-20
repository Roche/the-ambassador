package com.filipowm.ambassador.gradle.jooq

import com.filipowm.ambassador.gradle.utils.ClassLoaderHelper
import com.filipowm.ambassador.gradle.utils.DatabaseInit
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Strategy
import java.io.File
import java.util.Optional
import javax.inject.Inject

@CacheableTask
open class JooqGenerate @Inject constructor(
    @Input val configProvider: Property<Configuration>,
    runtimeClasspath: FileCollection
) : DefaultTask() {

    @get:Classpath
    val runtimeClasspath: ConfigurableFileCollection = project.objects.fileCollection().from(runtimeClasspath)
    private val outputDir: Provider<Directory>

    init {
        val outputDirNormalized = project.layout.projectDirectory
            .dir(project.providers.provider { configProvider.get().generator.target.directory })
            .orElse(project.layout.buildDirectory.dir("generated-src/jooq"))
        outputDir = project.objects.directoryProperty().value(outputDirNormalized)
    }

    @InputDirectory
    @Classpath
    fun getDatabaseMigrationFilesPath(): File {
        val path = Optional.ofNullable(project.extensions
                                           .getByType(JavaPluginExtension::class.java)
                                           .sourceSets
                                           .filter { "main" == it.name }
                                           .map { it.resources.sourceDirectories.asPath }
                                           .map { "$it/db/migration" }
                                           .firstOrNull())
            .orElseGet { "db/migration" }
        return File(path)
    }

    @OutputDirectory
    fun getOutputDir(): Provider<Directory> {
        return outputDir
    }

    @TaskAction
    fun generate() {
        logger.info("Starting generation of jOOQ classes")
        val config = configProvider.get()
        config.generator.target.directory = outputDir.get().asFile.absolutePath

        // abort if cleaning of output directory is disabled
        ensureTargetIsCleaned(config)
        trimConfiguration(config)

        logger.lifecycle(
            "Using {} driver to generate configuration in {}, accessible under package {}", config.jdbc.driver,
            config.generator.target.directory, config.generator.target.packageName
        )
        executeJooq(config)
        logger.info("jOOQ configuration generated")
    }

    private fun assertDatabaseDriverInClasspath(driver: String) {
        try {
            Thread.currentThread().contextClassLoader.loadClass(driver)
        } catch (e: ClassNotFoundException) {
            throw ClassNotFoundException("Unable to find '$driver' driver in classpath. Add 'jooqGenerator(\"$driver\")' to module dependencies")
        }
    }

    private fun ensureTargetIsCleaned(configuration: Configuration) {
        if (!configuration.generator.target.isClean) {
            throw GradleException(
                "generator.target.clean must not be set to false. " +
                    "Disabling the cleaning of the output directory can lead to unexpected behavior in a Gradle build."
            )
        }
    }

    private fun trimConfiguration(configuration: Configuration) {
        // avoid default value (name) being written even when matchers are configured
        val generator: Generator? = configuration.generator
        if (generator != null) {
            val strategy: Strategy? = generator.strategy
            if (strategy?.matchers != null) {
                strategy.name = null
            }
        }

        // avoid JDBC element being written when it has an empty configuration
        val jdbc: Jdbc = configuration.jdbc
        if (jdbc.driver == null && jdbc.url == null
            && jdbc.schema == null && jdbc.user == null
            && jdbc.username == null && jdbc.password == null
            && jdbc.isAutoCommit == null && jdbc.properties.isEmpty()
        ) {
            configuration.jdbc = null
        }
    }

    private fun executeJooq(config: Configuration) {
        ClassLoaderHelper.withAdditionalClasspath(runtimeClasspath.files) {
            assertDatabaseDriverInClasspath(config.jdbc.driver)
            try {
                DatabaseInit.load()
                setupDatabaseInitConfiguration()
                GenerationTool().run(config)
            } finally {
                DatabaseInit.ConfigHolder.clear()
            }
        }
    }

    private fun setupDatabaseInitConfiguration() {
        val path = getDatabaseMigrationFilesPath().path
        val init = DatabaseInit.InitConfig("filesystem:${path}")
        DatabaseInit.ConfigHolder.set(init)
    }

}