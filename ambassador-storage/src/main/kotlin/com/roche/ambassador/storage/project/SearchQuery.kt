package com.roche.ambassador.storage.project

import com.roche.ambassador.model.project.Visibility
import java.util.*

data class SearchQuery(
    val query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL
) {
    companion object {
        fun of(query: String? = null, visibility: Visibility = Visibility.INTERNAL): SearchQuery {
            return SearchQuery(Optional.ofNullable(query), visibility)
        }
    }
}
