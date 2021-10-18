package com.filipowm.gitlab.api.project.model


import com.fasterxml.jackson.annotation.JsonProperty

data class GroupSamlIdentity(
    @JsonProperty("extern_uid")
    val externUid: String? = null,
    @JsonProperty("provider")
    val provider: String? = null,
    @JsonProperty("saml_provider_id")
    val samlProviderId: Int? = null
)