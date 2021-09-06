package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.gitlab.api.model.Visibility as GitLabVisibility

object VisibilityMapper {
    private val mapping = mapOf(
        Pair(GitLabVisibility.INTERNAL, Visibility.INTERNAL),
        Pair(GitLabVisibility.PRIVATE, Visibility.PRIVATE),
        Pair(GitLabVisibility.PUBLIC, Visibility.PUBLIC)
    )

    private val reverseMapping = mapping.map { Pair(it.value, it.key) }.toMap()

    fun fromGitLab(visibility: GitLabVisibility): Visibility {
        return mapping.getOrDefault(visibility, Visibility.UNKNOWN)
    }

    fun fromAmbassador(visibility: Visibility): GitLabVisibility {
        return reverseMapping.getOrDefault(visibility, GitLabVisibility.PRIVATE)
    }
}
