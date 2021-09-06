package com.filipowm.ambassador.storage

import com.filipowm.ambassador.model.Visibility
import java.util.*

data class SearchQuery(
    val query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL
)
