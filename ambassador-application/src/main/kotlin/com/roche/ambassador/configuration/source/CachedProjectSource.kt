package com.roche.ambassador.configuration.source

import com.roche.ambassador.model.project.Member
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Timeline
import org.springframework.cache.Cache
import kotlin.reflect.KClass

// add methods which are expected to be cached (that's why not everything is cached as of now)
open class CachedProjectSource(
    private val delegate: ProjectSource,
    private val cache: Cache
) : ProjectSource by delegate {

    override suspend fun readCommits(projectId: String, ref: String): Timeline {
        return withCache("commits_${projectId}_$ref", Timeline::class) {
            delegate.readCommits(projectId, ref)
        }
    }

    override suspend fun readMembers(projectId: String): List<Member> = withCache("members_$projectId", List::class) {
        delegate.readMembers(projectId)
    } as List<Member>

    // @Cacheable is not used because it does not work with suspend, cause it adds Continuation as last arg and each exec is treated as unique call
    private suspend fun <T : Any> withCache(key: String, type: KClass<T>, dataReader: suspend () -> T): T {
        val cached = cache.get(key, type.java)
        return if (cached == null) {
            val freshData = dataReader()
            cache.put(key, freshData)
            freshData
        } else {
            cached
        }
    }
}
