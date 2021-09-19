package com.filipowm.ambassador.configuration.source

import com.filipowm.ambassador.GenerationSpec
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties.System.FAKE
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties.System.GITLAB
import com.filipowm.ambassador.document.TextAnalyzingService
import com.filipowm.ambassador.exceptions.AmbassadorException
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.fake.FakeSource
import com.filipowm.ambassador.gitlab.GitLabProjectMapper
import com.filipowm.ambassador.gitlab.GitLabSource
import com.filipowm.ambassador.storage.ProjectEntityRepository
import com.filipowm.gitlab.api.GitLab
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
open class ProjectSourceConfiguration {

    private val log by LoggerDelegate()

    @Bean
    open fun sources(
        projectSourcesProperties: ProjectSourcesProperties,
        textAnalyzingService: TextAnalyzingService,
        repository: ProjectEntityRepository
    ): ProjectSources {
        val source = when (projectSourcesProperties.system) {
            GITLAB -> configureGitLab(projectSourcesProperties, textAnalyzingService)
            FAKE -> configureFake(repository)
            else -> throw AmbassadorException("Unsupported source system: ${projectSourcesProperties.system}")
        }
        return ProjectSources(mapOf(projectSourcesProperties.name to source))
    }

    private fun configureFake(repository: ProjectEntityRepository): FakeSource {
        val spec = GenerationSpec(30000, true)
        if (spec.cleanRepositoryBefore) {
            log.info("Cleaning up fake repository first")
//            repository.deleteAll()
        }
        return FakeSource(spec)
    }

    private fun configureGitLab(
        projectSourcesProperties: ProjectSourcesProperties,
        textAnalyzingService: TextAnalyzingService
    ): GitLabSource {

        val gitlabApi = GitLab.builder()
            .retry()
                .maxAttempts(10)
                .exponentialBackoff(2.0, Duration.ofMinutes(5))
                .build()
            .authenticated().withPersonalAccessToken(projectSourcesProperties.token)
            .url(projectSourcesProperties.url)
            .build()
        val gitlabMapper = GitLabProjectMapper(gitlabApi, textAnalyzingService)
        return GitLabSource(gitlabApi, gitlabMapper)
    }
}
