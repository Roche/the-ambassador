package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.utils.QueryParam

data class ProjectQuery(
    @property:QueryParam("statistics") val withStatistics: Boolean = false,
    @property:QueryParam("license") val withLicense: Boolean = false,
    @property:QueryParam("with_custom_attributes") val withCustomAttributes: Boolean = false,
)