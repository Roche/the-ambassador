package com.roche.ambassador.gitlab

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.stats.Statistics
import java.util.*
import com.roche.gitlab.api.project.model.Project as GitLabProject

internal object GitLabProjectMapper {
    private val log by LoggerDelegate()

    fun mapGitLabProjectToOpenSourceProject(gitlabProject: GitLabProject): Project {
        log.debug("Mapping project {} to OS project", gitlabProject.name)
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
            Statistics(0, 0, 0, 0, 0, 0, 0, 0, 0)
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
        )
    }

    private fun toRelativePath(url: String?, ref: String?): String? {
        if (url == null || ref == null) {
            return null
        }
        val refPart = "/$ref/"
        val toCut = url.indexOf(refPart)
        return url.substring(toCut + refPart.length)
    }
}
