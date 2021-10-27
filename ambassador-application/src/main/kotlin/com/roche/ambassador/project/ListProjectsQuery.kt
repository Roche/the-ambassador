package com.roche.ambassador.project

import com.roche.ambassador.model.project.Visibility
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

data class ListProjectsQuery(
    @RequestParam("visibility", required = false) var visibility: Optional<Visibility> = Optional.of(Visibility.INTERNAL),
    @RequestParam("query", required = false) var query: Optional<String> = Optional.empty()
)
