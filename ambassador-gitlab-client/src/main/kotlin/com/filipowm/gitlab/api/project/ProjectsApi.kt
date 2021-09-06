package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.Api
import com.filipowm.gitlab.api.project.model.Project
import com.filipowm.gitlab.api.utils.Page
import com.filipowm.gitlab.api.utils.PageProvider
import com.filipowm.gitlab.api.utils.Pager
import com.filipowm.gitlab.api.utils.Pagination
import io.ktor.client.*
import java.util.stream.Stream

class ProjectsApi(basePath: String, httpClient: HttpClient) : Api(basePath, httpClient) {

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

    suspend fun stream(
        projectListQuery: ProjectListQuery = ProjectListQuery(),
        pagination: Pagination = Pagination()
    ): Stream<Project> {
        return paging(projectListQuery, pagination).stream()
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
