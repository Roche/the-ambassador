package com.filipowm.ambassador.configuration.source

import com.filipowm.ambassador.GenerationSpec
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties.System.FAKE
import com.filipowm.ambassador.configuration.source.ProjectSourcesProperties.System.GITLAB
import com.filipowm.ambassador.fake.FakeSource
import com.filipowm.ambassador.gitlab.GitLabSource
import com.filipowm.gitlab.api.GitLab
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
        val spec = GenerationSpec(30000, true)
        return FakeSource(spec)
    }

    private fun configureGitLab(
        projectSourcesProperties: ProjectSourcesProperties
    ): GitLabSource {

        val gitlabApi = GitLab.builder()
            .retry()
                .maxAttempts(10)
                .exponentialBackoff(2.0, Duration.ofMinutes(5))
                .build()
            .authenticated().withPersonalAccessToken(projectSourcesProperties.token)
            .url(projectSourcesProperties.url)
            .build()
        return GitLabSource(gitlabApi)
    }
}
