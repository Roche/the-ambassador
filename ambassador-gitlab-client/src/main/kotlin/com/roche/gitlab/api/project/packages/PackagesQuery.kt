package com.roche.gitlab.api.project.packages

import com.roche.gitlab.api.utils.QueryParam

data class PackagesQuery(
    @QueryParam("package_type") val type: SimplePackage.Type? = null,
    @QueryParam("package_name") val name: String? = null,
    @QueryParam("include_versionless") val includeVersionless: Boolean? = null,
    @QueryParam("status") val status: SimplePackage.Status? = null,
)
