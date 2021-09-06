package com.filipowm.ambassador.gitlab

import com.filipowm.ambassador.exceptions.Exceptions
import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.model.ProjectMapper
import com.filipowm.ambassador.model.SourceProjectRepository
import com.filipowm.gitlab.api.GitLab
import com.filipowm.gitlab.api.project.ProjectListQuery
import com.filipowm.gitlab.api.project.ProjectQuery
import com.filipowm.gitlab.api.utils.Pagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.*
import com.filipowm.gitlab.api.project.model.Project as GitLabProject

class GitLabSourceRepository(
    private val gitlab: GitLab,
    private val gitLabProjectMapper: GitLabProjectMapper
) : SourceProjectRepository<GitLabProject> {

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

    override suspend fun getByPath(path: String): Optional<Project> {
        return getById(path)
    }

    override suspend fun flow(filter: ProjectFilter): Flow<GitLabProject> {
        val visibility = VisibilityMapper.fromAmbassador(filter.visibility!!)
        val query = ProjectListQuery(
            withStatistics = true,
            withCustomAttributes = false,
            archived = filter.archived,
            visibility = visibility
        )
        return channelFlow {
            val pager = gitlab.projects().paging(query, Pagination(itemsPerPage = 10))
            for (page in pager) {
                for (project in page) {
                    log.warn("Publishing project {}", project.id)
                    send(project)
                }
            }
        }
    }

    override fun mapper(): ProjectMapper<GitLabProject> = gitLabProjectMapper::mapGitLabProjectToOpenSourceProject

}


