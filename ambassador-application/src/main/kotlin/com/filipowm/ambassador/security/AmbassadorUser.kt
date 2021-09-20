package com.filipowm.ambassador.security

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

data class AmbassadorUser(
    private val name: String,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val webUrl: String?,
    private val attributes: Map<String, Any>,
    private val authorities: List<GrantedAuthority>
) : OAuth2User {

    constructor(
        name: String,
        username: String,
        email: String,
        attributes: Map<String, Any>,
        authorities: List<String>
    ) : this(name, username, email, null, null, attributes, authorities.map { SimpleGrantedAuthority(it) })

    val isAdmin: Boolean = authorities.any { it.authority == ADMIN }

    @JsonIgnore
    val isUser: Boolean = authorities.any { it.authority == USER }

    @JsonGetter("name")
    fun getFullName(): String = name

    @JsonIgnore
    override fun getName(): String = username
    override fun getAttributes(): Map<String, Any> = attributes
    override fun getAuthorities(): List<GrantedAuthority> = authorities

    companion object Roles {
        const val ADMIN: String = "ROLE_ADMIN"
        const val USER: String = "ROLE_USER"
        const val ADMIN_AUTHORITY: String = "hasAuthority(\"$ADMIN\")"
    }
}
