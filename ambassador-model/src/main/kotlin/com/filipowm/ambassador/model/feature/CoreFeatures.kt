package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.FeatureReader
import com.filipowm.ambassador.model.Importance
import com.filipowm.ambassador.model.IndexEntry
import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.Contributor
import com.filipowm.ambassador.model.project.Contributors
import com.filipowm.ambassador.model.project.ProtectedBranch
import com.filipowm.ambassador.model.project.Visibility
import com.filipowm.ambassador.model.stats.Timeline

class ContributorsFeature(private val contributors: List<Contributor>?) : AbstractFeature<List<Contributor>>(contributors, "Contributors", importance = Importance.high()) {

    override fun asIndexEntry(): IndexEntry {
        if (contributors == null) {
            return IndexEntry.no()
        }
        val contributorsAggregate = Contributors(contributors.size, contributors.sortedByDescending { it.commits }.take(3))
        return IndexEntry.of("contributors", contributorsAggregate)
    }

    companion object : FeatureReaderFactory<ContributorsFeature> {
        override fun create(): FeatureReader<ContributorsFeature> {
            return FeatureReader.create() { project, source ->
                val contributors = source.readContributors(project.id.toString())
                ContributorsFeature(contributors)
            }
        }
    }
}

class LanguagesFeature(value: Map<String, Float>?) : AbstractFeature<Map<String, Float>>(value, "Languages") {

    companion object : FeatureReaderFactory<LanguagesFeature> {
        override fun create(): FeatureReader<LanguagesFeature> {
            return FeatureReader.create() { project, source ->
                val languages = source.readLanguages(project.id.toString())
                LanguagesFeature(languages)
            }
        }
    }
}

class StarsFeature(value: Int?) : NotIndexableFeature<Int>(value, "Stars") {
    companion object : FeatureReaderFactory<StarsFeature> {
        override fun create() = FeatureReader.createForProject() { StarsFeature(it.stats.forks) }
    }
}

class ForksFeature(value: Int?) : NotIndexableFeature<Int>(value, "Forks") {
    companion object : FeatureReaderFactory<ForksFeature> {
        override fun create() = FeatureReader.createForProject() { ForksFeature(it.stats.forks) }
    }
}

//class ReadmeFeature(value: Documentation) : AbstractFeature<Documentation>(value, "Readme") {
//    companion object : FeatureReaderFactory<ReadmeFeature> {
//        override fun create() = FeatureReader.createForFile("README.md") { ReadmeFeature() }
//    }
//}
//class ContributingGuideFeature(value: Documentation) : AbstractFeature<Documentation>(value, "Contributing Guide") {
//    companion object : FeatureReaderFactory<GitignoreFeature> {
//        override fun create() = FeatureReader.createForFile(".gitignore") { GitignoreFeature(it) }
//    }
//}
//class LicenseFeature(value: License) : AbstractFeature<License>(value, "License") {
//    companion object : FeatureReaderFactory<GitignoreFeature> {
//        override fun create() = FeatureReader.createForFile(".gitignore") { GitignoreFeature(it) }
//    }
//}
class CiDefinitionFeature(value: RawFile?) : AbstractFeature<RawFile>(value, "CI definition") {
    companion object : FeatureReaderFactory<CiDefinitionFeature> {
        override fun create() = FeatureReader.createForFile(".gitlab-ci.yml") { CiDefinitionFeature(it) }
    }
}
class ChangelogFeature(value: RawFile?) : AbstractFeature<RawFile>(value, "Changelog") {
    companion object : FeatureReaderFactory<ChangelogFeature> {
        override fun create() = FeatureReader.createForFile("CHANGELOG.md") { ChangelogFeature(it) }
    }
}
class GitignoreFeature(value: RawFile?) : AbstractFeature<RawFile>(value, ".gitignore") {
    companion object : FeatureReaderFactory<GitignoreFeature> {
        override fun create() = FeatureReader.createForFile(".gitignore") { GitignoreFeature(it) }
    }
}

class TagsFeature(value: List<String>?) : NotIndexableFeature<List<String>>(value, "Tags") {
    companion object : FeatureReaderFactory<TagsFeature> {
        override fun create() = FeatureReader.createForProject() { TagsFeature(it.tags) }
    }
}
class VisibilityFeature(value: Visibility?) : NotIndexableFeature<Visibility>(value, "Visibility") {
    companion object : FeatureReaderFactory<VisibilityFeature> {
        override fun create() = FeatureReader.createForProject() { VisibilityFeature(it.visibility) }
    }
}
class ProtectedBranchesFeature(value: List<ProtectedBranch>?) : AbstractFeature<List<ProtectedBranch>>(value, "Protected branches") {
    companion object : FeatureReaderFactory<ProtectedBranchesFeature> {
        override fun create() = FeatureReader.create() { project, source ->
            val protectedBranches = source.readProtectedBranches(project.id.toString())
            ProtectedBranchesFeature(protectedBranches)
        }
    }
}
class CommitsFeature(value: Timeline?) : TimelineFeature(value, "Commits") {
    companion object : FeatureReaderFactory<CommitsFeature> {
        override fun create() = FeatureReader.create() { project, source ->
            val commitsTimeline = source.readCommits(project.id.toString(), project.defaultBranch!!)
            CommitsFeature(commitsTimeline)
        }
    }
}
class ReleasesFeature(value: Timeline?) : TimelineFeature(value, "Releases") {
    companion object : FeatureReaderFactory<ReleasesFeature> {
        override fun create() = FeatureReader.create() { project, source ->
            val releases = source.readReleases(project.id.toString())
            ReleasesFeature(releases)
        }
    }
}
