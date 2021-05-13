package pl.filipowm.innersource.ambassador.configuration.source

import org.gitlab4j.api.GitLabApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.filipowm.innersource.ambassador.commons.exceptions.AmbassadorException
import pl.filipowm.innersource.ambassador.configuration.source.ProjectSourceProperties.System.GITLAB
import pl.filipowm.innersource.ambassador.gitlab.GitLabProjectRepository
import pl.filipowm.innersource.ambassador.model.ProjectRepository

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun projectSourceRepository(
        projectSourceProperties: ProjectSourceProperties
    ): ProjectRepository = when (projectSourceProperties.system) {
        GITLAB -> configureGitLab(projectSourceProperties)
        else -> throw AmbassadorException("Unsupported source system: ${projectSourceProperties.system}")
    }

    private fun configureGitLab(
        projectSourceProperties: ProjectSourceProperties
    ): ProjectRepository {
        val gitlabApi = GitLabApi(projectSourceProperties.url, projectSourceProperties.token)
        return GitLabProjectRepository(gitlabApi)
    }
}