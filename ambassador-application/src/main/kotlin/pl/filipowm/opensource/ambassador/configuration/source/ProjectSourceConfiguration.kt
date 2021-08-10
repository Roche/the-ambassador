package pl.filipowm.opensource.ambassador.configuration.source

import org.gitlab4j.api.GitLabApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.configuration.source.ProjectSourceProperties.System.GITLAB
import pl.filipowm.opensource.ambassador.document.TextAnalyzingService
import pl.filipowm.opensource.ambassador.exceptions.AmbassadorException
import pl.filipowm.opensource.ambassador.gitlab.GitLabProjectMapper
import pl.filipowm.opensource.ambassador.gitlab.GitLabSourceRepository

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun projectSourceRepository(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService,
        concurrencyProvider: ConcurrencyProvider
    ): GitLabSourceRepository = when (projectSourceProperties.system) {
        GITLAB -> configureGitLab(projectSourceProperties, textAnalyzingService, concurrencyProvider)
        else -> throw AmbassadorException("Unsupported source system: ${projectSourceProperties.system}")
    }

    private fun configureGitLab(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService,
        concurrencyProvider: ConcurrencyProvider
    ): GitLabSourceRepository {
        val gitlabApi = GitLabApi(projectSourceProperties.url, projectSourceProperties.token)
        val gitlabMapper = GitLabProjectMapper(gitlabApi, textAnalyzingService, concurrencyProvider)
        return GitLabSourceRepository(gitlabApi, gitlabMapper)
    }
}