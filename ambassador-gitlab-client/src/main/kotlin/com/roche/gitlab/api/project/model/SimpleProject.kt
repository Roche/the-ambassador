package com.roche.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

open class SimpleProject(
    @JsonProperty("avatar_url")
    open val avatarUrl: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    open val createdAt: LocalDateTime? = null,
    @JsonProperty("default_branch")
    open val defaultBranch: String? = null,
    @JsonProperty("description")
    open val description: String? = null,
    @JsonProperty("forks_count")
    open val forksCount: Int? = null,
    @JsonProperty("http_url_to_repo")
    open val httpUrlToRepo: String? = null,
    @JsonProperty("id")
    open val id: Int? = null,
    @JsonProperty("last_activity_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    open val lastActivityAt: LocalDateTime? = null,
    @JsonProperty("name")
    open val name: String,
    @JsonProperty("name_with_namespace")
    open val nameWithNamespace: String? = null,
    @JsonProperty("namespace")
    open val namespace: Namespace? = null,
    @JsonProperty("path")
    open val path: String,
    @JsonProperty("path_with_namespace")
    open val pathWithNamespace: String? = null,
    @JsonProperty("readme_url")
    open val readmeUrl: String? = null,
    @JsonProperty("ssh_url_to_repo")
    open val sshUrlToRepo: String? = null,
    @JsonProperty("star_count")
    open val starCount: Int? = null,
    @JsonProperty("tag_list")
    open val tagList: List<String>? = null,
    @JsonProperty("topics")
    open val topics: List<String>? = null,
    @JsonProperty("web_url")
    open val webUrl: String? = null,
) {

    @JsonProperty("namespace_id")
    fun getNamespaceId(): Long? {
        return namespace?.id
    }
}
