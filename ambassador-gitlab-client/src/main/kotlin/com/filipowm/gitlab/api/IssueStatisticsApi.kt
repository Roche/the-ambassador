package com.filipowm.gitlab.api

import com.filipowm.gitlab.api.model.IssueStatistics
import com.filipowm.gitlab.api.model.IssueStatisticsQuery
import io.ktor.client.*
import java.util.*

class IssueStatisticsApi(basePath: String, httpClient: HttpClient) : Api(basePath, httpClient) {

    suspend fun get(query: IssueStatisticsQuery = IssueStatisticsQuery()): IssueStatistics {
        return doGet(query)
    }

    suspend fun getOptional(query: IssueStatisticsQuery = IssueStatisticsQuery()): Optional<IssueStatistics> = doGetOptional(query)

}