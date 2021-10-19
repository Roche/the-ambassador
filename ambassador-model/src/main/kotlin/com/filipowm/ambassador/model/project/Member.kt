package com.filipowm.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("id", "username", "email", "name")
data class Member(val id: Long, val name: String, val email: String?, val username: String, val accessLevel: AccessLevel)