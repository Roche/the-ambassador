package com.roche.ambassador.gitlab

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.stats.Statistics
import com.roche.gitlab.api.project.model.FeatureAccessLevel
import com.roche.gitlab.api.project.model.NamespaceKind
import java.util.*
import com.roche.gitlab.api.project.model.Project as GitLabProject

internal object GitLabProjectMapper {
    private val log by LoggerDelegate()

    fun mapGitLabProjectToOpenSourceProject(gitlabProject: GitLabProject): Project {
        log.debug("Mapping project {} to Ambassador project", gitlabProject.name)
        val visibility = VisibilityMapper.fromGitLab(gitlabProject.visibility!!)
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
                namespace.id!!, namespace.webUrl!!, namespace.avatarUrl, null, namespace.name!!,
                namespace.fullPath!!, type, null
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
}
