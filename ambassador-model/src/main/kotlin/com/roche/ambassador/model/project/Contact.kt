package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("name", "email", "webUrl")
data class Contact(val name: String, val email: String?, val webUrl: String?, val avatarUrl: String?)
