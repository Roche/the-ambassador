package com.roche.ambassador.projects

import com.roche.ambassador.model.Visibility
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

data class ListProjectsQuery(
    @RequestParam("visibility", required = false) var visibility: Optional<Visibility> = Optional.of(Visibility.INTERNAL),
    @RequestParam("query", required = false) var query: Optional<String> = Optional.empty(),
    @RequestParam("topics", required = false) var topics: List<String> = listOf(),
    @RequestParam("language", required = false) var language: Optional<String> = Optional.empty()
)
