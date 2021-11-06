import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    idea
    `maven-publish`
}

idea {
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
}

fun createMergeTask(outputName: String): RegisteringDomainObjectDelegateProviderWithTypeAndAction<out TaskContainer, ReportMergeTask> {
    return tasks.registering(ReportMergeTask::class) {
        output.set(rootProject.buildDir.resolve("reports/detekt/$outputName"))
    }
}

val reportMergeSarif by createMergeTask("merge.sarif")
val reportMergeXml by createMergeTask("merge.xml")

subprojects {
    plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin::class) {
        tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class) detekt@{
            finalizedBy(reportMergeSarif)
            finalizedBy(reportMergeXml)

            reportMergeSarif.configure {
                input.from(this@detekt.sarifReportFile)
            }

            reportMergeXml.configure {
                input.from(this@detekt.xmlReportFile)
            }
        }
    }
}