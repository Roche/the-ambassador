package com.filipowm.gitlab.api.project.pipelines

import com.filipowm.gitlab.api.utils.QueryParam
import java.time.LocalDateTime

data class PipelinesQuery(
    @QueryParam("scope") val type: SimplePipeline.Scope? = null,
    @QueryParam("status") val status: SimplePipeline.Status? = null,
    @QueryParam("source") val source: String? = null,
    @QueryParam("ref") val ref: String? = null,
    @QueryParam("sha") val sha: String? = null,
    @QueryParam("yaml_errors") val includeWithYamlErrors: Boolean? = null,
    @QueryParam("username") val username: String? = null,
    @QueryParam("updated_after") val updatedAfter: LocalDateTime? = null,
    @QueryParam("updated_before") val updatedBefore: LocalDateTime? = null,
)