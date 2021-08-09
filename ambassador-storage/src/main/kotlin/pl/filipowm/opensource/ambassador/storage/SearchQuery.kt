package pl.filipowm.opensource.ambassador.storage

import pl.filipowm.opensource.ambassador.model.Visibility
import java.util.*

data class SearchQuery(
    val query: Optional<String>,
    val visibility: Visibility = Visibility.INTERNAL
)
