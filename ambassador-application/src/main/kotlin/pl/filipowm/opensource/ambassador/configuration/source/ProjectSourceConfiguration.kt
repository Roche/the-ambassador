package pl.filipowm.opensource.ambassador.configuration.source

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.filipowm.opensource.ambassador.configuration.source.ProjectSourceProperties.System.GITLAB
import pl.filipowm.opensource.ambassador.document.TextAnalyzingService
import pl.filipowm.opensource.ambassador.exceptions.AmbassadorException
import pl.filipowm.opensource.ambassador.gitlab.GitLabProjectMapper
import pl.filipowm.opensource.ambassador.gitlab.GitLabSourceRepository
import pl.filipowm.opensource.ambassador.gitlab.api.GitLabApiBuilder

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun projectSourceRepository(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService
    ): GitLabSourceRepository = when (projectSourceProperties.system) {
        GITLAB -> configureGitLab(projectSourceProperties, textAnalyzingService)
        else -> throw AmbassadorException("Unsupported source system: ${projectSourceProperties.system}")
    }

    private fun configureGitLab(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService
    ): GitLabSourceRepository {

        val gitlabApi = GitLabApiBuilder(projectSourceProperties.url, projectSourceProperties.token)
            .enableExceptionHandling()
            .build()
        val gitlabMapper = GitLabProjectMapper(gitlabApi, textAnalyzingService)
        return GitLabSourceRepository(gitlabApi, gitlabMapper)
    }
}