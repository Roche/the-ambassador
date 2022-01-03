package com.roche.gitlab.api.user


import com.fasterxml.jackson.annotation.JsonProperty

data class Identity(
    @JsonProperty("extern_uid")
    val externUid: String,
    @JsonProperty("provider")
    val provider: String
)