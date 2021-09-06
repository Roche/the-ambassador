package com.filipowm.gitlab.api

import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.model.IssueStatistics
import com.filipowm.gitlab.api.model.IssueStatisticsQuery
import java.util.*

class IssueStatisticsApi(basePath: String, httpClient: GitLabHttpClient) : Api(basePath, httpClient) {

    suspend fun get(query: IssueStatisticsQuery = IssueStatisticsQuery()): IssueStatistics {
        return doGet(query)
    }

    suspend fun getOptional(query: IssueStatisticsQuery = IssueStatisticsQuery()): Optional<IssueStatistics> = doGetOptional(query)
}
