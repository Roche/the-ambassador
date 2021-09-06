package pl.filipowm.opensource.ambassador.gradle.jooq

//import pl.filipowm.opensource.ambassador.gradle.jooq.util.Objects.cloneObject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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
import pl.filipowm.opensource.ambassador.gradle.utils.ClassLoaderHelper
import pl.filipowm.opensource.ambassador.gradle.utils.DatabaseInit
import javax.inject.Inject

abstract class JooqGenerate @Inject constructor(
    @Input val configProvider: Property<Configuration>,
    runtimeClasspath: FileCollection
) : DefaultTask() {

    @get:Classpath
    val runtimeClasspath = project.objects.fileCollection().from(runtimeClasspath)
    private val outputDir: Provider<Directory>
    private val allInputsDeclared: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    @Internal
    fun getAllInputsDeclared(): Property<Boolean> {
        return allInputsDeclared
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
        val jdbc: Jdbc = configuration.getJdbc()
        if (jdbc.getDriver() == null && jdbc.getUrl() == null
            && jdbc.getSchema() == null && jdbc.getUser() == null
            && jdbc.getUsername() == null && jdbc.getPassword() == null
            && jdbc.isAutoCommit() == null && jdbc.getProperties().isEmpty()
        ) {
            configuration.setJdbc(null)
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
        val resourcesPath = project.extensions
            .getByType(JavaPluginExtension::class.java)
            .sourceSets
            .filter { "main" == it.name }
            .map { it.resources.sourceDirectories.asPath }
            .first()

        val init = DatabaseInit.InitConfig(resourcesPath)
        DatabaseInit.ConfigHolder.set(init)
    }

    init {
        val outputDirNormalized = project.layout.projectDirectory
            .dir(project.providers.provider { configProvider.get().generator.target.directory })
            .orElse(project.layout.buildDirectory.dir("generated-src/jooq"))
        outputDir = project.objects.directoryProperty().value(outputDirNormalized)

        // do not use lambda due to a bug in Gradle 6.5
        outputs.upToDateWhen { allInputsDeclared.get() }
    }

}