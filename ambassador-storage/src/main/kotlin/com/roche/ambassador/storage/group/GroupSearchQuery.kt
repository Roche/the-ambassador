package com.roche.ambassador.storage.group

import com.roche.ambassador.model.Visibility
import com.roche.ambassador.storage.search.SearchQuery
import java.util.*

class GroupSearchQuery(
    query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL
) : SearchQuery(query) {
    companion object {
        fun of(query: String? = null, visibility: Visibility = Visibility.INTERNAL): GroupSearchQuery {
            return GroupSearchQuery(Optional.ofNullable(query), visibility)
        }
    }
}
