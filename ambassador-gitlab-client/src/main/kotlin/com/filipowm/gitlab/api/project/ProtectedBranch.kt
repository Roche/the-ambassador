package com.filipowm.gitlab.api.project

import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.gitlab.api.model.AccessLevel

data class ProtectedBranch(
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("merge_access_levels")
    val mergeAccessLevels: List<AccessLevel> = listOf(),
    @JsonProperty("push_access_levels")
    val pushAccessLevels: List<AccessLevel> = listOf(),
    @JsonProperty("allow_force_push")
    val allowForcePush: Boolean,
    @JsonProperty("code_owner_approval_required")
    val codeOwnerApprovalRequired: Boolean? = null
)