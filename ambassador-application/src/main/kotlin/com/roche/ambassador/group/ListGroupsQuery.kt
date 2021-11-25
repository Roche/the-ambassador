package com.roche.ambassador.group

import com.roche.ambassador.model.Visibility
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

data class ListGroupsQuery(
    @RequestParam("visibility", required = false) var visibility: Optional<Visibility> = Optional.of(Visibility.INTERNAL),
    @RequestParam("query", required = false) var query: Optional<String> = Optional.empty()
)
