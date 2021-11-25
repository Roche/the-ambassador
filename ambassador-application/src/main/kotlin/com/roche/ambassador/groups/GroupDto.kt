package com.roche.ambassador.groups

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.projects.SimpleProjectDto

data class GroupDto(@JsonUnwrapped val group: Group, val projects: List<SimpleProjectDto>)
