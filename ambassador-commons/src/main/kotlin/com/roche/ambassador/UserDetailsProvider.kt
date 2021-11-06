package com.roche.ambassador

interface UserDetailsProvider {

    fun getName(): String
    fun getUsername(): String
    fun getEmail(): String
    fun getAvatarUrl(): String
    fun getWebUrl(): String
    fun isAdmin(): Boolean
}
