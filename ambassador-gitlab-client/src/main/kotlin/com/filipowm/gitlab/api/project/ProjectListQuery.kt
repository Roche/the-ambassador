package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.model.AccessLevelName
import com.filipowm.gitlab.api.model.Visibility
import com.filipowm.gitlab.api.utils.QueryParam
import java.util.*

data class ProjectListQuery(
    @QueryParam val archived: Boolean? = null,
    @QueryParam val visibility: Visibility? = null,
    @QueryParam val search: String? = null,
    @QueryParam("search_namespaces") val searchNamespaces: Boolean? = null,
    @QueryParam val simple: Boolean? = null,
    @QueryParam val owned: Boolean? = null,
    @QueryParam val membership: Boolean? = null,
    @QueryParam val starred: Boolean? = null,
    @QueryParam("statistics") val withStatistics: Boolean? = null,
    @QueryParam("with_custom_attributes") val withCustomAttributes: Boolean? = null,
    @QueryParam("with_issues_enabled") val withIssuesEnabled: Boolean? = null,
    @QueryParam("with_merge_requests_enabled") val withMergeRequestsEnabled: Boolean? = null,
    @QueryParam("with_programming_language") val withProgrammingLanguage: String? = null,
    @QueryParam("wiki_checksum_failed") val wikiChecksumFailed: Boolean? = null,
    @QueryParam("repository_checksum_failed") val repositoryChecksumFailed: Boolean? = null,
    @QueryParam("min_access_level") val minAccessLevel: AccessLevelName? = null,
    @QueryParam("id_after") val idAfter: Int? = null,
    @QueryParam("id_before") val idBefore: Int? = null,
    @QueryParam("last_activity_after") val lastActivityAfter: Date? = null,
    @QueryParam("last_activity_before") val lastActivityBefore: Date? = null,
    @QueryParam("repository_storage") val repositoryStorage: String? = null,
    @QueryParam val topic: String? = null,
)
