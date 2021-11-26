package com.roche.gitlab.api.project.events

import com.roche.gitlab.api.Api
import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.Pager
import com.roche.gitlab.api.utils.Pagination

class EventsApi(basePath: String, client: GitLabHttpClient) : Api(basePath, client) {

    suspend fun list(query: EventsListQuery, pagination: Pagination = Pagination()): List<Event> {
        return paging(query, pagination).get()
    }

    suspend fun paging(query: EventsListQuery, fromPagination: Pagination = Pagination()): Pager<Event> = Pager(fromPagination) { doGetPage(it, query) }
}