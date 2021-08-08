package pl.filipowm.opensource.ambassador.gitlab

import pl.filipowm.opensource.ambassador.model.Visibility

object VisibilityMapper {

    private val mapping = mapOf(
        Pair(org.gitlab4j.api.models.Visibility.INTERNAL, Visibility.INTERNAL),
        Pair(org.gitlab4j.api.models.Visibility.PRIVATE, Visibility.PRIVATE),
        Pair(org.gitlab4j.api.models.Visibility.PUBLIC, Visibility.PUBLIC)
    )

    private val reverseMapping = mapping.map { Pair(it.value, it.key) }.toMap()

    fun fromGitLab(visibility: org.gitlab4j.api.models.Visibility): Visibility {
        return mapping.getOrDefault(visibility, Visibility.UNKNOWN)
    }

    fun fromAmbassador(visibility: Visibility): org.gitlab4j.api.models.Visibility {
        return reverseMapping.getOrDefault(visibility, org.gitlab4j.api.models.Visibility.PRIVATE)
    }
}