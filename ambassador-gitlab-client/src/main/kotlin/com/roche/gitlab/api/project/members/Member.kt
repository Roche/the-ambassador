package com.roche.gitlab.api.project.members

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.gitlab.api.model.AccessLevelName
import com.roche.gitlab.api.model.UserState
import java.time.LocalDate

@JsonPropertyOrder("id", "username", "email", "name")
data class Member(
    @JsonProperty("access_level")
    val accessLevel: AccessLevelName? = null,
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("email")
    val email: String? = null,
    @JsonProperty("expires_at")
    val expiresAt: LocalDate? = null,
    @JsonProperty("group_saml_identity")
    val groupSamlIdentity: GroupSamlIdentity? = null,
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("state")
    val state: UserState,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("web_url")
    val webUrl: String? = null
)
