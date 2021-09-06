package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SharedWithGroup(
    @JsonProperty("group_access_level")
    var groupAccessLevel: Int,
    @JsonProperty("group_full_path")
    var groupFullPath: String,
    @JsonProperty("group_id")
    var groupId: Int,
    @JsonProperty("group_name")
    var groupName: String
)