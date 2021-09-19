package com.filipowm.ambassador.security

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

data class AmbassadorUser(private val name: String,
                          val username: String,
                          val email: String,
                          val avatarUrl: String,
                          val webUrl: String,
                          private val attributes: Map<String, Any>,
                          private val authorities: List<GrantedAuthority>) : OAuth2User {

    val isAdmin = authorities.any { it.authority == ADMIN }
    @JsonIgnore
    val isUser = authorities.any { it.authority == USER }

    @JsonGetter("name")
    fun getFullName() = name
    @JsonIgnore
    override fun getName() = username
    override fun getAttributes() = attributes
    override fun getAuthorities() = authorities

    companion object Roles {
        const val ADMIN = "ROLE_ADMIN"
        const val USER = "ROLE_USER"
        const val ADMIN_AUTHORITY = "hasAuthority(\"$ADMIN\")"
        const val USER_AUTHORITY = "hasAuthority(\"$USER\")"
    }
}