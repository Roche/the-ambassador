package com.roche.ambassador.gitlab

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.PullRequest
import com.roche.ambassador.model.stats.Statistics
import com.roche.gitlab.api.project.mergerequests.MergeRequest
import com.roche.gitlab.api.project.model.FeatureAccessLevel
import com.roche.gitlab.api.project.model.NamespaceKind
import java.util.*
import com.roche.gitlab.api.groups.Group as GitLabGroup
import com.roche.gitlab.api.groups.Statistics as GitLabStatistics
import com.roche.gitlab.api.project.model.Project as GitLabProject

internal object GitLabMapper {
    private val log by LoggerDelegate()

    fun fromGitLabGroup(gitlabGroup: GitLabGroup): Group {
        log.debug("Mapping group {} to Ambassador group", gitlabGroup.name)
        val visibility = VisibilityMapper.fromGitLab(gitlabGroup.visibility)
        return Group(
            gitlabGroup.id, gitlabGroup.name, gitlabGroup.fullPath!!, gitlabGroup.description,
            gitlabGroup.webUrl!!, gitlabGroup.avatarUrl, visibility,
            gitlabGroup.createdAt?.toLocalDate(), Group.Type.UNKNOWN, gitlabGroup.parentId,
            if (gitlabGroup.statistics != null) {
                createStatistics(gitlabGroup.statistics!!)
            } else {
                null
            }
        )
    }

    private fun createStatistics(stats: GitLabStatistics): Statistics {
        return Statistics(
            null, null, null, stats.jobArtifactsSize,
            stats.lfsObjectsSize, stats.packagesSize, stats.repositorySize, stats.storageSize, stats.wikiSize
        )
    }

    fun fromGitLabProject(gitlabProject: GitLabProject): Project {
        log.debug("Mapping project {} to Ambassador project", gitlabProject.name)
        val visibility = VisibilityMapper.fromGitLab(gitlabProject.visibility)
        val stats = if (gitlabProject.statistics != null) {
            val glStats = gitlabProject.statistics!!
            Statistics(
                gitlabProject.forksCount!!,
                gitlabProject.starCount!!,
                glStats.commitCount,
                glStats.jobArtifactsSize,
                glStats.lfsObjectsSize,
                glStats.packagesSize,
                glStats.repositorySize,
                glStats.storageSize,
                glStats.wikiSize
            )
        } else {
            Statistics(gitlabProject.forksCount!!, gitlabProject.starCount!!, 0, 0, 0, 0, 0, 0, 0)
        }
        val namespace = gitlabProject.namespace
        val group = if (namespace != null) {
            val type = when (namespace.kind) {
                NamespaceKind.GROUP -> Group.Type.GROUP
                NamespaceKind.USER -> Group.Type.USER
                else -> Group.Type.UNKNOWN
            }
            Group(
                namespace.id!!, namespace.name!!, namespace.fullPath!!, null,
                namespace.webUrl!!, namespace.avatarUrl, null, null,
                type, null
            )
        } else {
            null
        }
        return Project(
            id = gitlabProject.id!!.toLong(),
            url = gitlabProject.webUrl,
            avatarUrl = gitlabProject.avatarUrl,
            name = gitlabProject.name,
            description = gitlabProject.description,
            visibility = visibility,
            stats = stats,
            tags = Optional.ofNullable(gitlabProject.tagList).orElseGet { listOf() },
            createdDate = gitlabProject.createdAt!!.toLocalDate(),
            lastActivityDate = gitlabProject.lastActivityAt!!.toLocalDate(),
            defaultBranch = gitlabProject.defaultBranch,
            potentialReadmePath = toRelativePath(gitlabProject.readmeUrl, gitlabProject.defaultBranch),
            potentialLicensePath = toRelativePath(gitlabProject.licenseUrl, gitlabProject.defaultBranch),
            archived = gitlabProject.archived ?: false,
            empty = gitlabProject.emptyRepo ?: false,
            forked = gitlabProject.isForked(),
            permissions = Permissions(canEveryoneAccess(gitlabProject.forkingAccessLevel), canEveryoneAccess(gitlabProject.mergeRequestsAccessLevel)),
            fullName = gitlabProject.pathWithNamespace ?: gitlabProject.path,
            parent = group,
        )
    }

    private fun canEveryoneAccess(featureAccessLevel: FeatureAccessLevel?): Boolean = featureAccessLevel?.canEveryoneAccess() ?: false

    private fun toRelativePath(url: String?, ref: String?): String? {
        if (url == null || ref == null) {
            return null
        }
        val refPart = "/$ref/"
        val toCut = url.indexOf(refPart)
        return url.substring(toCut + refPart.length)
    }

    fun fromGitLabState(state: MergeRequest.State): PullRequest.State {
        return when(state) {
            MergeRequest.State.MERGED -> PullRequest.State.MERGED
            MergeRequest.State.CLOSED -> PullRequest.State.CLOSED
            else -> PullRequest.State.OPEN
        }
    }
}
