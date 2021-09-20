package com.filipowm.gitlab.api.model

import com.filipowm.gitlab.api.utils.QueryParam
import java.time.LocalDateTime

data class IssueStatisticsQuery(
    @QueryParam val labels: List<String>? = null,
    @QueryParam val milestone: String? = null,
    @QueryParam val scope: QueryScope? = null,
    @QueryParam("author_id") val authorId: Int? = null,
    @QueryParam("author_username") val authorUsername: String? = null,
    @QueryParam("assignee_id") val assigneeId: Int? = null,
    @QueryParam("assignee_username") val assigneeUsername: String? = null,
    @QueryParam("my_reaction_emoji") val myReactionEmoji: String? = null,
    @QueryParam val search: String? = null,
    @QueryParam("created_after") val createdAfter: LocalDateTime? = null,
    @QueryParam("created_before") val createdBefore: LocalDateTime? = null,
    @QueryParam("updated_after") val updatedAfter: LocalDateTime? = null,
    @QueryParam("updated_before") val updatedBefore: LocalDateTime? = null,
    @QueryParam val confidential: Boolean? = null
)
