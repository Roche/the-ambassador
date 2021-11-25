package com.roche.ambassador.groups

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.group.Group
import java.time.LocalDate

@JsonPropertyOrder("id", "name", "description", "url")
class SimpleGroupDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val url: String,
    val avatarUrl: String? = null,
    val visibility: Visibility? = null,
    val createdDate: LocalDate? = null,
    val type: Group.Type? = null,
    val score: Double? = null,
    val criticalityScore: Double? = null,
    val activityScore: Double? = null,
) {

    companion object {
        fun from(group: Group): SimpleGroupDto {
            return SimpleGroupDto(
                group.id, group.name, group.description,
                group.url, group.avatarUrl, group.visibility,
                group.createdDate, group.type, group.scores?.total,
                group.scores?.criticality, group.scores?.activity
            )
        }
    }
}
