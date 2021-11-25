package com.roche.gitlab.api.groups

import com.roche.gitlab.api.utils.QueryParam

data class GroupsListQuery(
    @QueryParam val statistics: Boolean? = null,
    @QueryParam val search: String? = null,
    @QueryParam("all_available") val allAvailable: Boolean? = null,
    @QueryParam val owned: Boolean? = null,
    @QueryParam("with_custom_attributes") val withCustomAttributes: Boolean? = null,
    @QueryParam("top_level_only") val topLevelOnly: Boolean? = null,) {
}