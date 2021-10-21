package com.filipowm.gitlab.api.project.mergerequests

import com.fasterxml.jackson.annotation.JsonProperty

data class IssueStats(
    @JsonProperty("closed")
    val closed: Int = 0,
    @JsonProperty("total")
    val total: Int = 0
) {
    fun active(): Int = total - closed
}
