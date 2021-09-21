package com.filipowm.ambassador.storage.project

import com.filipowm.ambassador.model.project.Visibility
import java.util.*

data class SearchQuery(
    val query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL
)
