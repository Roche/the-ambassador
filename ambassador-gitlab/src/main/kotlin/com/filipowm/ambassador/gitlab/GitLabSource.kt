package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.model.source.ForkedProjectCriteria
import com.filipowm.ambassador.model.source.InvalidProjectCriteria
import com.filipowm.ambassador.model.source.PersonalProjectCriteria
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.gitlab.api.GitLab
import com.filipowm.gitlab.api.project.ProjectListQuery
import com.filipowm.gitlab.api.project.ProjectQuery
import com.filipowm.gitlab.api.utils.Pagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.*
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

class GitLabSource(
    private val gitlab: GitLab,
    private val gitLabProjectMapper: GitLabProjectMapper
) : ProjectSource<GitLabProject> {

    companion object {
        private val log by LoggerDelegate()
    }

    override suspend fun getById(id: String): Optional<Project> {
        log.info("Reading project {}", id)
        val prj = gitlab.projects()
            .withId(id.toLong())
            .get(ProjectQuery(true, true, true))
            .orElseThrow { Exceptions.NotFoundException("Project with ID $id not found") }
        return Optional.ofNullable(gitLabProjectMapper.mapGitLabProjectToOpenSourceProject(prj))
    }

    override suspend fun flow(filter: ProjectFilter): Flow<GitLabProject> {
        val visibility = VisibilityMapper.fromAmbassador(filter.visibility!!)
        val query = ProjectListQuery(
            withStatistics = true,
            withCustomAttributes = false,
            archived = filter.archived,
            visibility = visibility,
            lastActivityAfter = filter.lastActivityAfter
        )
        return channelFlow {
            val pager = gitlab.projects().paging(query, Pagination(itemsPerPage = 50))
            for (page in pager) {
                for (project in page) {
                    log.debug("Publishing project {}", project.id)
                    send(project)
                }
            }
        }
    }

    override fun getName() = "GitLab"

    override suspend fun map(input: GitLabProject) = gitLabProjectMapper.mapGitLabProjectToOpenSourceProject(input)

    override fun getInvalidProjectCriteria(): InvalidProjectCriteria<GitLabProject> = GitLabInvalidProjectCriteria
    override fun resolveName(project: GitLabProject): String = project.nameWithNamespace!!
    override fun resolveId(project: GitLabProject): String = project.id!!.toString()
    override fun getForkedProjectCriteria(): ForkedProjectCriteria<GitLabProject> = GitLabForkedProjectCriteria
    override fun getPersonalProjectCriteria(): PersonalProjectCriteria<GitLabProject> = GitLabPersonalProjectCriteria
}
