package com.roche.gitlab.api.project.packages

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class SimplePackage(
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("_links")
    val links: PackageLinks? = null,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("package_type")
    val packageType: Type,
    @JsonProperty("tags")
    val tags: List<String> = listOf(),
    @JsonProperty("version")
    val version: String? = null
) {
    enum class Type {
        NPM,
        MAVEN,
        CONAN,
        PYPI,
        COMPOSER,
        NUGET,
        HELM,
        GOLANG,
        GENERIC
    }

    enum class Status {
        DEFAULT,
        HIDDEN,
        PROCESSING
    }
}
