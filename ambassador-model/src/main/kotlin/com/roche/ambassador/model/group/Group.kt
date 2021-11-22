package com.roche.ambassador.model.group

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("id", "name", "fullName", "type", "url")
data class Group(
    val id: Long,
    val url: String,
    val avatarUrl: String? = null,
    val description: String? = null,
    val name: String,
    val fullName: String,
    val type: Type,
    val parent: Group? = null
) {
    enum class Type {
        GROUP,
        USER,
        UNKNOWN
    }
}