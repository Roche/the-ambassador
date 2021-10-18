package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.filipowm.gitlab.api.model.AccessLevelName
import com.filipowm.gitlab.api.utils.Dates
import java.time.LocalDateTime

@JsonPropertyOrder("id", "username", "email", "name")
data class Member(
    @JsonProperty("access_level")
    val accessLevel: AccessLevelName? = null,
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("email")
    val email: String? = null,
    @JsonProperty("expires_at")
    @JsonFormat(pattern = Dates.ISO_DATE_TIME_FORMAT)
    val expiresAt: LocalDateTime? = null,
    @JsonProperty("group_saml_identity")
    val groupSamlIdentity: GroupSamlIdentity? = null,
    @JsonProperty("id")
    val id: Long? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("username")
    val username: String? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null
)