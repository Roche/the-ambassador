package com.roche.ambassador.model.feature

import com.roche.ambassador.model.FeatureReader

object FeatureReaders {

    private val readers: List<FeatureReader<*>> = listOf(
        StarsFeature.create(),
        ForksFeature.create(),
        LanguagesFeature.create(),
        ContributorsFeature.create(),
        TagsFeature.create(),
        VisibilityFeature.create(), CommitsFeature.create(),
        ReleasesFeature.create(),
        ProtectedBranchesFeature.create(),
        ReadmeFeature.create(),
        ContributingGuideFeature.create(),
        LicenseFeature.create(),
        LastActivityDateFeature.create(),
        CreatedDateFeature.create(),
        DescriptionFeature.create(),
        IssuesFeature.create(),
        MembersFeature.create(),
    )

    fun all(): List<FeatureReader<*>> {
        return readers
    }
}
