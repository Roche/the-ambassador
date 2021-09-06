package com.filipowm.gitlab.api.project.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Permissions(
    @JsonProperty("group_access")
    var groupAccess: GroupAccess?,
    @JsonProperty("project_access")
    var projectAccess: ProjectAccess?
)