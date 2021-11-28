package com.roche.ambassador.storage.project

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.storage.search.SearchQuery
import java.util.*

data class ProjectSearchQuery(
    override val query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL,
    val language: String? = null,
    val tags: List<String> = listOf()
) : SearchQuery(query) {
    companion object {
        fun of(query: String? = null, visibility: Visibility = Visibility.INTERNAL): ProjectSearchQuery {
            return ProjectSearchQuery(Optional.ofNullable(query), visibility)
        }
    }
}
