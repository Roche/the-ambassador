package com.roche.ambassador.group

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.project.SimpleProjectDto

data class GroupDto(@JsonUnwrapped val group: Group, val projects: List<SimpleProjectDto>)
