package pl.filipowm.opensource.ambassador.configuration.source

import org.gitlab4j.api.GitLabApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.filipowm.opensource.ambassador.ConcurrencyProvider
import pl.filipowm.opensource.ambassador.commons.exceptions.AmbassadorException
import pl.filipowm.opensource.ambassador.configuration.source.ProjectSourceProperties.System.GITLAB
import pl.filipowm.opensource.ambassador.document.TextAnalyzingService
import pl.filipowm.opensource.ambassador.gitlab.GitLabProjectRepository
import pl.filipowm.opensource.ambassador.gitlab.ProjectMapper
import pl.filipowm.opensource.ambassador.model.ProjectRepository

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun projectSourceRepository(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService,
        concurrencyProvider: ConcurrencyProvider
    ): ProjectRepository = when (projectSourceProperties.system) {
        GITLAB -> configureGitLab(projectSourceProperties, textAnalyzingService, concurrencyProvider)
        else -> throw AmbassadorException("Unsupported source system: ${projectSourceProperties.system}")
    }

    private fun configureGitLab(
        projectSourceProperties: ProjectSourceProperties,
        textAnalyzingService: TextAnalyzingService,
        concurrencyProvider: ConcurrencyProvider
    ): ProjectRepository {
        val gitlabApi = GitLabApi(projectSourceProperties.url, projectSourceProperties.token)
        val gitlabMapper = ProjectMapper(gitlabApi, textAnalyzingService, concurrencyProvider)
        return GitLabProjectRepository(gitlabApi, gitlabMapper, concurrencyProvider)
    }
}