package com.roche.ambassador.model.feature

import com.roche.ambassador.model.FeatureReader

object FeatureReaders {

    private val readers: List<FeatureReader<*>> = listOf(
        DefaultBranchFeature.create(),
        StarsFeature.create(),
        ForksFeature.create(),
        LanguagesFeature.create(),
        ContributorsFeature.create(),
        TopicsFeature.create(),
        VisibilityFeature.create(),
        CommitsFeature.create(),
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
        CommentsFeature.create(),
        PullRequestsFeature.create(),
        CiExecutionsFeature.create(),
        PermissionsFeature.create()
    )

    fun getProjectBasedReaders(): List<FeatureReader<*>> {
        return readers.filter { !it.isUsingExternalSource() }
    }

    fun all(): List<FeatureReader<*>> = readers
}
