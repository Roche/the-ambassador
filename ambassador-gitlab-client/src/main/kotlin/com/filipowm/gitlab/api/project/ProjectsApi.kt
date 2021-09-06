package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.project.model.Project
import com.filipowm.gitlab.api.utils.Page
import com.filipowm.gitlab.api.utils.PageProvider
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination

class ProjectsApi(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun create(project: Project): Project {
        return doPost(body = project)
    }

    suspend fun paging(
        projectListQuery: ProjectListQuery = ProjectListQuery(),
        pagination: Pagination = Pagination()
    ): Pager<Project> {
        val pageProvider: PageProvider<Project> = {
            doGetPage(it, projectListQuery)
        }
        return Pager(pagination, pageProvider)
    }

    suspend fun getPage(
        projectListQuery: ProjectListQuery = ProjectListQuery(),
        pagination: Pagination = Pagination()
    ): Page<Project> {
        return doGetPage(pagination, projectListQuery)
    }

    fun withId(id: Long): ProjectApi {
        return withPath(id.toString())
    }

    fun withPath(path: String): ProjectApi {
        return ProjectApi("$basePath/$path", client)
    }
}
