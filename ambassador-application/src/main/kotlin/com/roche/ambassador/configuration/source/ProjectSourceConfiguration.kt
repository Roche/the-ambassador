package com.roche.ambassador.configuration.source

import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.configuration.source.ProjectSourcesProperties.System.FAKE
import com.roche.ambassador.configuration.source.ProjectSourcesProperties.System.GITLAB
import com.roche.ambassador.fake.FakeSource
import com.roche.ambassador.gitlab.GitLabSource
import com.roche.ambassador.model.source.ProjectSources
import com.roche.gitlab.api.GitLab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
open class ProjectSourceConfiguration {

    @Bean
    open fun sources(
        projectSourcesProperties: ProjectSourcesProperties,
    ): ProjectSources {
        val source = when (projectSourcesProperties.system) {
            GITLAB -> configureGitLab(projectSourcesProperties)
            FAKE -> configureFake()
        }
        return ProjectSources(mapOf(projectSourcesProperties.name to source))
    }

    private fun configureFake(): FakeSource {
        val spec = GenerationSpec(5000, true)
        return FakeSource(spec)
    }

    @ExperimentalCoroutinesApi
    private fun configureGitLab(
        projectSourcesProperties: ProjectSourcesProperties
    ): GitLabSource {

        // @formatter:off
        val gitlabApi = GitLab.builder()
            .retry()
                .maxAttempts(10)
                .exponentialBackoff(2.0, Duration.ofMinutes(5))
                .build()
            .httpClient()
                .logging().nothing().and()
            .authenticated().withPersonalAccessToken(projectSourcesProperties.token)
            .url(projectSourcesProperties.url)
            .build()
        // @formatter:on
        return GitLabSource(projectSourcesProperties.name, gitlabApi)
    }
}
