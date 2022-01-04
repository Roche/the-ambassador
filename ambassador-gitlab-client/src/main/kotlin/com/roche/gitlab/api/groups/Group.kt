package com.roche.gitlab.api.groups

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.roche.gitlab.api.model.Visibility
import com.roche.gitlab.api.utils.Dates
import java.time.LocalDateTime

data class Group(
    @JsonProperty("auto_devops_enabled")
    val autoDevopsEnabled: Boolean? = null,
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("created_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val createdAt: LocalDateTime? = null,
    @JsonProperty("default_branch_protection")
    val defaultBranchProtection: Int? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("emails_disabled")
    val emailsDisabled: Boolean? = null,
    @JsonProperty("file_template_project_id")
    val fileTemplateProjectId: Long? = null,
    @JsonProperty("full_name")
    val fullName: String? = null,
    @JsonProperty("full_path")
    val fullPath: String? = null,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("lfs_enabled")
    val lfsEnabled: Boolean? = null,
    @JsonProperty("mentions_disabled")
    val mentionsDisabled: Boolean? = null,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("parent_id")
    val parentId: Long? = null,
    @JsonProperty("path")
    val path: String? = null,
    @JsonProperty("project_creation_level")
    val projectCreationLevel: String? = null,
    @JsonProperty("request_access_enabled")
    val requestAccessEnabled: Boolean? = null,
    @JsonProperty("require_two_factor_authentication")
    val requireTwoFactorAuthentication: Boolean? = null,
    @JsonProperty("share_with_group_lock")
    val shareWithGroupLock: Boolean? = null,
    @JsonProperty("statistics")
    val statistics: Statistics? = null,
    @JsonProperty("subgroup_creation_level")
    val subgroupCreationLevel: String? = null,
    @JsonProperty("two_factor_grace_period")
    val twoFactorGracePeriod: Int? = null,
    @JsonProperty("visibility")
    val visibility: Visibility,
    @JsonProperty("web_url")
    val webUrl: String? = null
)
