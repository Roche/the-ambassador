package com.filipowm.ambassador.configuration.source

import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties.System.GITLAB
import com.filipowm.ambassador.document.TextAnalyzingService
import com.filipowm.ambassador.exceptions.AmbassadorException
import com.filipowm.ambassador.gitlab.GitLabProjectMapper
import com.filipowm.ambassador.gitlab.GitLabSource
import com.filipowm.gitlab.api.GitLab
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun sources(
        projectSourcesProperties: ProjectSourcesProperties,
        textAnalyzingService: TextAnalyzingService
    ): ProjectSources {
        val source = when (projectSourcesProperties.system) {
            GITLAB -> configureGitLab(projectSourcesProperties, textAnalyzingService)
            else -> throw AmbassadorException("Unsupported source system: ${projectSourcesProperties.system}")
        }
        return ProjectSources(mapOf(projectSourcesProperties.name to source))
    }

    private fun configureGitLab(
        projectSourcesProperties: ProjectSourcesProperties,
        textAnalyzingService: TextAnalyzingService
    ): GitLabSource {

        val gitlabApi = GitLab.builder()
            .authenticated().withPersonalAccessToken(projectSourcesProperties.token)
            .url(projectSourcesProperties.url)
            .build()
        val gitlabMapper = GitLabProjectMapper(gitlabApi, textAnalyzingService)
        return GitLabSource(gitlabApi, gitlabMapper)
    }
}
