package com.roche.ambassador.model.group

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.score.Scores
import com.roche.ambassador.model.stats.Statistics
import java.time.LocalDate

@JsonPropertyOrder("id", "name", "fullName", "type", "url")
data class Group(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String? = null,
    val url: String,
    val avatarUrl: String? = null,
    val visibility: Visibility? = null,
    val createdDate: LocalDate? = null,
    val type: Type? = null,
    val parentId: Long? = null,
    val stats: Statistics? = null,
    val scores: Scores? = null
) {
    enum class Type {
        GROUP,
        USER,
        UNKNOWN
    }
}
