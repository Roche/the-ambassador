package com.filipowm.ambassador.configuration.source

import com.filipowm.ambassador.configuration.source.ProjectSourceProperties.System.GITLAB
import com.filipowm.ambassador.document.TextAnalyzingService
import com.filipowm.ambassador.exceptions.AmbassadorException
import com.filipowm.ambassador.gitlab.GitLabProjectMapper
import com.filipowm.ambassador.gitlab.GitLabSourceRepository
import com.filipowm.gitlab.api.GitLab
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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

        val gitlabApi = GitLab.builder()
            .authenticated().withPersonalAccessToken(projectSourceProperties.token)
            .url(projectSourceProperties.url)
            .build()
        val gitlabMapper = GitLabProjectMapper(gitlabApi, textAnalyzingService)
        return GitLabSourceRepository(gitlabApi, gitlabMapper)
    }
}
