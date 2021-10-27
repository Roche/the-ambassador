package com.roche.gitlab.api

import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.model.IssueStatistics
import com.roche.gitlab.api.model.IssueStatisticsQuery
import java.util.*

class IssueStatisticsApi(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun get(query: IssueStatisticsQuery = IssueStatisticsQuery()): IssueStatistics {
        return doGet(query)
    }

    suspend fun getOptional(query: IssueStatisticsQuery = IssueStatisticsQuery()): Optional<IssueStatistics> = doGetOptional(query)
}
