package com.roche.ambassador.model.feature

import com.roche.ambassador.extensions.substringWithFullWords
import com.roche.ambassador.markdown.MarkdownParser
import com.roche.ambassador.model.FeatureReader
import com.roche.ambassador.model.Importance
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.files.ExcerptFile
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.project.*
import com.roche.ambassador.model.project.ci.CiExecutions
import com.roche.ambassador.model.project.ci.CiStabilityConfiguration
import com.roche.ambassador.model.project.ci.CiStabilityPolicy
import com.roche.ambassador.model.stats.Timeline
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors

val parser = MarkdownParser()

class ContributorsFeature(contributors: List<Contributor>?) : AbstractFeature<List<Contributor>>(contributors, importance = Importance.high()) {
    companion object : FeatureReaderFactory<ContributorsFeature> {
        override fun create(): FeatureReader<ContributorsFeature> {
            return FeatureReader.create { project, source ->
                val contributors = source.readContributors(project.id.toString())
                ContributorsFeature(contributors)
            }
        }
    }
}

class LanguagesFeature(value: Map<String, Float>?) : AbstractFeature<Map<String, Float>>(value) {

    companion object : FeatureReaderFactory<LanguagesFeature> {
        override fun create(): FeatureReader<LanguagesFeature> {
            return FeatureReader.create { project, source ->
                val languages = source.readLanguages(project.id.toString())
                LanguagesFeature(languages)
            }
        }
    }
}

class StarsFeature(value: Int?) : NotIndexableFeature<Int>(value) {
    companion object : FeatureReaderFactory<StarsFeature> {
        override fun create(): FeatureReader<StarsFeature> = FeatureReader.createForProject { StarsFeature(it.stats.stars) }
    }
}

class ForksFeature(value: Int?) : NotIndexableFeature<Int>(value) {
    companion object : FeatureReaderFactory<ForksFeature> {
        override fun create(): FeatureReader<ForksFeature> = FeatureReader.createForProject { ForksFeature(it.stats.forks) }
    }
}

class ReadmeFeature(value: ExcerptFile?) : FileFeature<ExcerptFile>(value) {
    companion object : FeatureReaderFactory<ReadmeFeature> {
        override fun create(): FeatureReader<ReadmeFeature> = FeatureReader.createForFile({ setOf(it.potentialReadmePath ?: "README.md") }) {
            val excerpt = Optional.ofNullable(it.content)
                .flatMap { content -> parser.parseSilently(content, "readme") }
                .map { doc -> doc.asText().substringWithFullWords(0, 3000) }
                .orElse(null)

            val mef = ExcerptFile(it.exists, it.hash, it.language, it.contentLength, it.url, excerpt)
            ReadmeFeature(mef)
        }
    }
}

class ContributingGuideFeature(value: RawFile?) : FileFeature<RawFile>(value) {
    companion object : FeatureReaderFactory<ContributingGuideFeature> {
        override fun create(): FeatureReader<ContributingGuideFeature> = FeatureReader.createForFile({ setOf("CONTRIBUTING.md", "CONTRIBUTING") }) { ContributingGuideFeature(it) }
    }
}

class LicenseFeature(value: RawFile?) : FileFeature<RawFile>(value) {
    companion object : FeatureReaderFactory<LicenseFeature> {
        override fun create(): FeatureReader<LicenseFeature> = FeatureReader.createForFile({ setOf(it.potentialLicensePath ?: "LICENSE") }) { LicenseFeature(it) }
    }
}

@Deprecated(message = "Not used in any score")
class CiDefinitionFeature(value: RawFile?) : FileFeature<RawFile>(value) {
    companion object : FeatureReaderFactory<CiDefinitionFeature> {
        override fun create(): FeatureReader<CiDefinitionFeature> = FeatureReader.createForFile(".gitlab-ci.yml") { CiDefinitionFeature(it) }
    }
}

@Deprecated(message = "Not used in any score")
class ChangelogFeature(value: RawFile?) : FileFeature<RawFile>(value) {
    companion object : FeatureReaderFactory<ChangelogFeature> {
        override fun create(): FeatureReader<ChangelogFeature> = FeatureReader.createForFile("CHANGELOG.md") { ChangelogFeature(it) }
    }
}

@Deprecated(message = "Not used in any score")
class GitignoreFeature(value: RawFile?) : FileFeature<RawFile>(value) {
    companion object : FeatureReaderFactory<GitignoreFeature> {
        override fun create(): FeatureReader<GitignoreFeature> = FeatureReader.createForFile(".gitignore") { GitignoreFeature(it) }
    }
}

class TopicsFeature(value: List<String>?) : NotIndexableFeature<List<String>>(value) {
    companion object : FeatureReaderFactory<TopicsFeature> {
        override fun create(): FeatureReader<TopicsFeature> = FeatureReader.createForProject { TopicsFeature(it.topics) }
    }
}

class VisibilityFeature(value: Visibility?) : NotIndexableFeature<Visibility>(value) {
    companion object : FeatureReaderFactory<VisibilityFeature> {
        override fun create(): FeatureReader<VisibilityFeature> = FeatureReader.createForProject { VisibilityFeature(it.visibility) }
    }
}

class CreatedDateFeature(value: LocalDate?) : DateFeature(value) {
    companion object : FeatureReaderFactory<CreatedDateFeature> {
        override fun create(): FeatureReader<CreatedDateFeature> = FeatureReader.createForProject { CreatedDateFeature(it.createdDate) }
    }

    override fun isIndexable(): Boolean = false
}

class LastActivityDateFeature(value: LocalDate?) : DateFeature(value) {
    companion object : FeatureReaderFactory<LastActivityDateFeature> {
        override fun create(): FeatureReader<LastActivityDateFeature> = FeatureReader.createForProject { LastActivityDateFeature(it.lastActivityDate) }
    }

    override fun isIndexable(): Boolean = false
}

class DefaultBranchFeature(value: String?) : NotIndexableFeature<String>(value) {
    companion object : FeatureReaderFactory<DefaultBranchFeature> {
        override fun create(): FeatureReader<DefaultBranchFeature> = FeatureReader.createForProject { DefaultBranchFeature(it.defaultBranch) }
    }
}

class DescriptionFeature(value: String?) : NotIndexableFeature<String>(value) {
    companion object : FeatureReaderFactory<DescriptionFeature> {
        override fun create(): FeatureReader<DescriptionFeature> = FeatureReader.createForProject { DescriptionFeature(it.description) }
    }
}

class ProtectedBranchesFeature(value: List<ProtectedBranch>?) : AbstractFeature<List<ProtectedBranch>>(value) {
    companion object : FeatureReaderFactory<ProtectedBranchesFeature> {
        override fun create(): FeatureReader<ProtectedBranchesFeature> = FeatureReader.create { project, source ->
            val protectedBranches = source.readProtectedBranches(project.id.toString())
            ProtectedBranchesFeature(protectedBranches)
        }
    }
}

class CommitsFeature(value: Timeline?) : TimelineFeature(value) {
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

class ReleasesFeature(value: Timeline?) : TimelineFeature(value) {
    companion object : FeatureReaderFactory<ReleasesFeature> {
        override fun create(): FeatureReader<ReleasesFeature> = FeatureReader.create { project, source ->
            val releases = source.readReleases(project.id.toString())
            ReleasesFeature(releases)
        }
    }
}

class IssuesFeature(value: Issues) : AbstractFeature<Issues>(value) {
    companion object : FeatureReaderFactory<IssuesFeature> {
        override fun create(): FeatureReader<IssuesFeature> = FeatureReader.create { project, source ->
            val issues = source.readIssues(project.id.toString())
            IssuesFeature(issues)
        }
    }
}

class MembersFeature(value: Map<AccessLevel, Int>) : AbstractFeature<Map<AccessLevel, Int>>(value) {
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

class PullRequestsFeature(value: PullRequests) : AbstractFeature<PullRequests>(value) {
    companion object : FeatureReaderFactory<PullRequestsFeature> {
        override fun create(): FeatureReader<PullRequestsFeature> = FeatureReader.create { project, source ->
            val pullRequests = source.readPullRequests(project.id.toString()).sortedByDescending { it.end ?: it.start }
            PullRequestsFeature(PullRequests(pullRequests))
        }
    }
}

class CiExecutionsFeature(value: CiExecutions) : AbstractFeature<CiExecutions>(value) {
    companion object : FeatureReaderFactory<CiExecutionsFeature> {

        private val config = CiStabilityConfiguration()

        override fun create(): FeatureReader<CiExecutionsFeature> = FeatureReader.create { project, source ->
            val ciExecutions = if (project.defaultBranch != null) {
                source.readCiExecutions(project.id.toString(), project.defaultBranch).sortedByDescending { it.end ?: it.start }
            } else {
                listOf()
            }
            val stability = if (ciExecutions.isNotEmpty()) {
                CiStabilityPolicy.calculateStability(ciExecutions, config)
            } else {
                null
            }
            val wrapped = CiExecutions(ciExecutions, stability)
            CiExecutionsFeature(wrapped)
        }
    }
}

class CommentsFeature(value: Timeline) : TimelineFeature(value) {
    companion object : FeatureReaderFactory<CommentsFeature> {
        override fun create(): FeatureReader<CommentsFeature> = FeatureReader.create { project, source ->
            val comments = source.readComments(project.id.toString())
            CommentsFeature(comments)
        }
    }
}
