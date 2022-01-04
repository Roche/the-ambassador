package com.roche.gitlab.api.groups

import com.roche.gitlab.api.model.AccessLevelName
import com.roche.gitlab.api.model.Visibility
import com.roche.gitlab.api.utils.QueryParam

data class GroupProjectListQuery(
    @QueryParam val archived: Boolean? = null,
    @QueryParam val visibility: Visibility? = null,
    @QueryParam val search: String? = null,
    @QueryParam val simple: Boolean? = null,
    @QueryParam val owned: Boolean? = null,
    @QueryParam val starred: Boolean? = null,
    @QueryParam("with_custom_attributes") val withCustomAttributes: Boolean? = null,
    @QueryParam("with_issues_enabled") val withIssuesEnabled: Boolean? = null,
    @QueryParam("with_merge_requests_enabled") val withMergeRequestsEnabled: Boolean? = null,
    @QueryParam("include_subgroups") val includeSubgroups: Boolean? = null,
    @QueryParam("with_shared") val withShared: Boolean? = null,
    @QueryParam("min_access_level") val minAccessLevel: AccessLevelName? = null,
)
