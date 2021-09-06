package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupAccess(
    @JsonProperty("access_level")
    var accessLevel: Int?,
    @JsonProperty("notification_level")
    var notificationLevel: Int?
)
