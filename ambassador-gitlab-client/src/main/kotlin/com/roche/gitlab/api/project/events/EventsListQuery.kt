package com.roche.gitlab.api.project.events

import com.roche.gitlab.api.utils.QueryParam
import java.time.LocalDate

data class EventsListQuery(
    @QueryParam("action") val action: String? = null,
    @QueryParam("target_type") val target: String? = null,
    @QueryParam("after") val after: LocalDate? = null,
    @QueryParam("before") val before: LocalDate? = null
)
