package com.roche.gitlab.api.user


import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,
    @JsonProperty("bio")
    val bio: String? = null,
    @JsonProperty("can_create_group")
    val canCreateGroup: Boolean? = null,
    @JsonProperty("can_create_project")
    val canCreateProject: Boolean? = null,
    @JsonProperty("color_scheme_id")
    val colorSchemeId: Int? = null,
    @JsonProperty("commit_email")
    val commitEmail: String? = null,
    @JsonProperty("confirmed_at")
    val confirmedAt: String? = null,
    @JsonProperty("created_at")
    val createdAt: String? = null,
    @JsonProperty("current_sign_in_at")
    val currentSignInAt: String? = null,
    @JsonProperty("current_sign_in_ip")
    val currentSignInIp: String? = null,
    @JsonProperty("email")
    val email: String? = null,
    @JsonProperty("external")
    val `external`: Boolean? = null,
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("identities")
    val identities: List<Identity>? = null,
    @JsonProperty("is_admin")
    val isAdmin: Boolean? = null,
    @JsonProperty("job_title")
    val jobTitle: String? = null,
    @JsonProperty("last_activity_on")
    val lastActivityOn: String? = null,
    @JsonProperty("last_sign_in_at")
    val lastSignInAt: String? = null,
    @JsonProperty("last_sign_in_ip")
    val lastSignInIp: String? = null,
    @JsonProperty("linkedin")
    val linkedin: String? = null,
    @JsonProperty("location")
    val location: Any? = null,
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("organization")
    val organization: String? = null,
    @JsonProperty("private_profile")
    val privateProfile: Boolean? = null,
    @JsonProperty("projects_limit")
    val projectsLimit: Int? = null,
    @JsonProperty("public_email")
    val publicEmail: String? = null,
    @JsonProperty("skype")
    val skype: String? = null,
    @JsonProperty("state")
    val state: String? = null,
    @JsonProperty("theme_id")
    val themeId: Int? = null,
    @JsonProperty("twitter")
    val twitter: String? = null,
    @JsonProperty("two_factor_enabled")
    val twoFactorEnabled: Boolean? = null,
    @JsonProperty("username")
    val username: String? = null,
    @JsonProperty("web_url")
    val webUrl: String? = null,
    @JsonProperty("website_url")
    val websiteUrl: String? = null
)