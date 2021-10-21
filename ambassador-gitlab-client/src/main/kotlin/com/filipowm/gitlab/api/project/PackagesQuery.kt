package com.filipowm.gitlab.api.project

import com.filipowm.gitlab.api.project.model.SimplePackage
import com.filipowm.gitlab.api.utils.QueryParam

data class PackagesQuery(
    @QueryParam("package_type") val type: SimplePackage.Type? = null,
    @QueryParam("package_name") val name: String? = null,
    @QueryParam("include_versionless") val includeVersionless: Boolean? = null,
    @QueryParam("status") val status: SimplePackage.Status? = null,
)