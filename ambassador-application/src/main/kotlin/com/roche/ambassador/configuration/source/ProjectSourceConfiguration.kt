package com.roche.ambassador.configuration.source

import com.roche.ambassador.ConcurrencyProvider
import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.configuration.source.ProjectSourcesProperties.System.FAKE
import com.roche.ambassador.configuration.source.ProjectSourcesProperties.System.GITLAB
import com.roche.ambassador.fake.FakeSource
import com.roche.ambassador.gitlab.GitLabSource
import com.roche.ambassador.model.source.ProjectSources
import com.roche.gitlab.api.GitLab
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ProjectSourceConfiguration {

    @Bean
    fun sources(
        projectSourcesProperties: ProjectSourcesProperties,
        @Qualifier("projectSourceCacheManager")
        cacheManager: CacheManager,
        concurrencyProvider: ConcurrencyProvider
    ): ProjectSources {
        val source = when (projectSourcesProperties.system) {
            GITLAB -> configureGitLab(projectSourcesProperties, concurrencyProvider)
            FAKE -> configureFake()
        }
        val cache = cacheManager.getCache(source.name())!!
        val cached = CachedProjectSource(source, cache)
        return ProjectSources(mapOf(projectSourcesProperties.name to cached))
    }

    private fun configureFake(): FakeSource {
        val spec = GenerationSpec(5000)
        return FakeSource(spec)
    }

    @ExperimentalCoroutinesApi
    private fun configureGitLab(
        projectSourcesProperties: ProjectSourcesProperties,
        concurrencyProvider: ConcurrencyProvider
    ): GitLabSource {

        // @formatter:off
        val gitlabApi = GitLab.builder()
            .retry()
            .maxAttempts(10)
            .exponentialBackoff(2.0, Duration.ofMinutes(5))
            .build()
            .httpClient()
            .clientThreadsCount(concurrencyProvider.getSourceClientThreadsCount())
            .logging().nothing().and()
            .authenticated().withPersonalAccessToken(projectSourcesProperties.token)
            .url(projectSourcesProperties.url)
            .build()
        // @formatter:on
        return GitLabSource(projectSourcesProperties.name, gitlabApi)
    }
}
