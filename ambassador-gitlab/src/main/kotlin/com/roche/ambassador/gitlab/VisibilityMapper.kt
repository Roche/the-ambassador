package com.roche.ambassador.gitlab

import com.roche.ambassador.model.Visibility
import com.roche.gitlab.api.model.Visibility as GitLabVisibility

internal object VisibilityMapper {
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
