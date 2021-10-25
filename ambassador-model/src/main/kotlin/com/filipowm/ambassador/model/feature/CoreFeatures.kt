package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.FeatureReader
import com.filipowm.ambassador.model.Importance
import com.filipowm.ambassador.model.IndexEntry
import com.filipowm.ambassador.model.files.ExcerptFile
import com.filipowm.ambassador.model.files.RawFile
import com.filipowm.ambassador.model.project.*
import com.filipowm.ambassador.model.stats.Timeline
import java.time.LocalDate
import java.util.stream.Collectors

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
            return FeatureReader.create { project, source ->
                val contributors = source.readContributors(project.id.toString())
                ContributorsFeature(contributors)
            }
        }
    }
}

class LanguagesFeature(value: Map<String, Float>?) : AbstractFeature<Map<String, Float>>(value, "Languages") {

    companion object : FeatureReaderFactory<LanguagesFeature> {
        override fun create(): FeatureReader<LanguagesFeature> {
            return FeatureReader.create { project, source ->
                val languages = source.readLanguages(project.id.toString())
                LanguagesFeature(languages)
            }
        }
    }
}

class StarsFeature(value: Int?) : NotIndexableFeature<Int>(value, "Stars") {
    companion object : FeatureReaderFactory<StarsFeature> {
        override fun create(): FeatureReader<StarsFeature> = FeatureReader.createForProject { StarsFeature(it.stats.forks) }
    }
}

class ForksFeature(value: Int?) : NotIndexableFeature<Int>(value, "Forks") {
    companion object : FeatureReaderFactory<ForksFeature> {
        override fun create(): FeatureReader<ForksFeature> = FeatureReader.createForProject { ForksFeature(it.stats.forks) }
    }
}

class ReadmeFeature(value: ExcerptFile?) : FileFeature<ExcerptFile>(value, "Readme") {
    companion object : FeatureReaderFactory<ReadmeFeature> {
        override fun create(): FeatureReader<ReadmeFeature> = FeatureReader.createForFile({ setOf(it.potentialReadmePath ?: "README.md") }) { ReadmeFeature(it.asExcerptFile()) }
    }
}

class ContributingGuideFeature(value: RawFile?) : FileFeature<RawFile>(value, "Contribution Guide") {
    companion object : FeatureReaderFactory<ContributingGuideFeature> {
        override fun create(): FeatureReader<ContributingGuideFeature> = FeatureReader.createForFile({ setOf("CONTRIBUTING.md", "CONTRIBUTING") }) { ContributingGuideFeature(it) }
    }
}

class LicenseFeature(value: RawFile?) : FileFeature<RawFile>(value, "License") {
    companion object : FeatureReaderFactory<LicenseFeature> {
        override fun create(): FeatureReader<LicenseFeature> = FeatureReader.createForFile({ setOf(it.potentialLicensePath ?: "LICENSE") }) { LicenseFeature(it) }
    }
}

class CiDefinitionFeature(value: RawFile?) : FileFeature<RawFile>(value, "CI definition") {
    companion object : FeatureReaderFactory<CiDefinitionFeature> {
        override fun create(): FeatureReader<CiDefinitionFeature> = FeatureReader.createForFile(".gitlab-ci.yml") { CiDefinitionFeature(it) }
    }
}

class ChangelogFeature(value: RawFile?) : FileFeature<RawFile>(value, "Changelog") {
    companion object : FeatureReaderFactory<ChangelogFeature> {
        override fun create(): FeatureReader<ChangelogFeature> = FeatureReader.createForFile("CHANGELOG.md") { ChangelogFeature(it) }
    }
}

class GitignoreFeature(value: RawFile?) : FileFeature<RawFile>(value, ".gitignore") {
    companion object : FeatureReaderFactory<GitignoreFeature> {
        override fun create(): FeatureReader<GitignoreFeature> = FeatureReader.createForFile(".gitignore") { GitignoreFeature(it) }
    }
}

class TagsFeature(value: List<String>?) : NotIndexableFeature<List<String>>(value, "Tags") {
    companion object : FeatureReaderFactory<TagsFeature> {
        override fun create(): FeatureReader<TagsFeature> = FeatureReader.createForProject { TagsFeature(it.tags) }
    }
}

class VisibilityFeature(value: Visibility?) : NotIndexableFeature<Visibility>(value, "Visibility") {
    companion object : FeatureReaderFactory<VisibilityFeature> {
        override fun create(): FeatureReader<VisibilityFeature> = FeatureReader.createForProject { VisibilityFeature(it.visibility) }
    }
}

class CreatedDateFeature(value: LocalDate?) : DateFeature(value, "Created date") {
    companion object : FeatureReaderFactory<CreatedDateFeature> {
        override fun create(): FeatureReader<CreatedDateFeature> = FeatureReader.createForProject { CreatedDateFeature(it.createdDate) }
    }

    override fun asIndexEntry(): IndexEntry = IndexEntry.no()
}

class LastActivityDateFeature(value: LocalDate?) : DateFeature(value, "Last activity date") {
    companion object : FeatureReaderFactory<LastActivityDateFeature> {
        override fun create(): FeatureReader<LastActivityDateFeature> = FeatureReader.createForProject { LastActivityDateFeature(it.lastActivityDate) }
    }

    override fun asIndexEntry(): IndexEntry = IndexEntry.no()
}

class DescriptionFeature(value: String?) : NotIndexableFeature<String>(value, "Description") {
    companion object : FeatureReaderFactory<DescriptionFeature> {
        override fun create(): FeatureReader<DescriptionFeature> = FeatureReader.createForProject { DescriptionFeature(it.description) }
    }
}

class ProtectedBranchesFeature(value: List<ProtectedBranch>?) : AbstractFeature<List<ProtectedBranch>>(value, "Protected branches") {
    companion object : FeatureReaderFactory<ProtectedBranchesFeature> {
        override fun create(): FeatureReader<ProtectedBranchesFeature> = FeatureReader.create { project, source ->
            val protectedBranches = source.readProtectedBranches(project.id.toString())
            ProtectedBranchesFeature(protectedBranches)
        }
    }
}

class CommitsFeature(value: Timeline?) : TimelineFeature(value, "Commits") {
    companion object : FeatureReaderFactory<CommitsFeature> {
        override fun create(): FeatureReader<CommitsFeature> = FeatureReader.create { project, source ->
            val commitsTimeline = if (project.defaultBranch == null) {
                Timeline()
            } else {
                source.readCommits(project.id.toString(), project.defaultBranch)
            }
            CommitsFeature(commitsTimeline)
        }
    }
}

class ReleasesFeature(value: Timeline?) : TimelineFeature(value, "Releases") {
    companion object : FeatureReaderFactory<ReleasesFeature> {
        override fun create(): FeatureReader<ReleasesFeature> = FeatureReader.create { project, source ->
            val releases = source.readReleases(project.id.toString())
            ReleasesFeature(releases)
        }
    }
}

class IssuesFeature(value: Issues) : AbstractFeature<Issues>(value, "Issues") {
    companion object : FeatureReaderFactory<IssuesFeature> {
        override fun create(): FeatureReader<IssuesFeature> = FeatureReader.create { project, source ->
            val issues = source.readIssues(project.id.toString())
            IssuesFeature(issues)
        }
    }
}

class MembersFeature(value: Map<AccessLevel, Int>) : AbstractFeature<Map<AccessLevel, Int>>(value, "Members") {
    companion object : FeatureReaderFactory<MembersFeature> {
        override fun create(): FeatureReader<MembersFeature> = FeatureReader.create { project, source ->
            val members = source.readMembers(project.id.toString())
            val memberByLevel = members
                .stream()
                .map { it.accessLevel }
                .collect(
                    Collectors.groupingBy({ it }, Collectors.summingInt { 1 }),
                )
            MembersFeature(memberByLevel)
        }
    }

}

class PullRequestsFeature(value: Timeline) : TimelineFeature(value, "Pull Requests") {
    companion object : FeatureReaderFactory<PullRequestsFeature> {
        override fun create(): FeatureReader<PullRequestsFeature> = FeatureReader.create { project, source ->
            val pullRequests = source.readPullRequests(project.id.toString())
            PullRequestsFeature(pullRequests)
        }
    }
}
