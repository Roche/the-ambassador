package com.roche.ambassador.model.feature

import com.roche.ambassador.collections.BiMap
import com.roche.ambassador.collections.HashBiMap
import com.roche.ambassador.model.Feature
import java.util.*
import kotlin.reflect.KClass

object FeatureNameLookup {

    private val lookup: BiMap<String, KClass<out Feature<*>>> = HashBiMap.create(
        mapOf(
            "commits" to CommitsFeature::class,
            "contributors" to ContributorsFeature::class,
            "contributingGuide" to ContributingGuideFeature::class,
            "readme" to ReadmeFeature::class,
            "languages" to LanguagesFeature::class,
            "stars" to StarsFeature::class,
            "forks" to ForksFeature::class,
            "readme" to ReadmeFeature::class,
            "license" to LicenseFeature::class,
            "ciDefinition" to CiDefinitionFeature::class,
            "changelog" to ChangelogFeature::class,
            "gitignore" to GitignoreFeature::class,
            "tags" to TagsFeature::class,
            "visibility" to VisibilityFeature::class,
            "createdDate" to CreatedDateFeature::class,
            "lastActivityDate" to LastActivityDateFeature::class,
            "description" to DescriptionFeature::class,
            "protectedBranches" to ProtectedBranchesFeature::class,
            "releases" to ReleasesFeature::class,
            "issues" to IssuesFeature::class,
            "members" to MembersFeature::class,
            "pullRequests" to PullRequestsFeature::class,
            "issuesComments" to CommentsFeature::class,
            "ci" to CiExecutionsFeature::class
        )
    )

    fun getFeatureName(type: KClass<out Feature<*>>): Optional<String> = Optional.ofNullable(lookup.inverse[type])

    fun getFeatureType(name: String): Optional<KClass<out Feature<*>>> = Optional.ofNullable(lookup[name])
}
