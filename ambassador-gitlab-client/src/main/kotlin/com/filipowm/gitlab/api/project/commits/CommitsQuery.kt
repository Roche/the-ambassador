package com.filipowm.gitlab.api.project.commits

import com.filipowm.gitlab.api.utils.QueryParam
import java.time.LocalDateTime

data class CommitsQuery(
    @QueryParam("ref_name") val refName: String? = null,
    @QueryParam val since: LocalDateTime? = null,
    @QueryParam val until: LocalDateTime? = null,
    @QueryParam val path: String? = null,
    @QueryParam val all: Boolean? = null,
    @QueryParam("with_stats") val withStats: Boolean? = null,
    @QueryParam("first_parent") val firstParent: Boolean? = null,
    @QueryParam("trailers") val includeTrailers: Boolean? = null
)
