package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.filipowm.gitlab.api.model.AccessLevelName
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