package com.roche.ambassador.projects

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class ProjectStatsHistoryQuery(
    @RequestParam("before", required = false) var before: Optional<LocalDate> = Optional.empty(),
    @RequestParam("after", required = false) var after: Optional<LocalDate> = Optional.empty()
) {
    @Hidden
    fun getAfterAsLocalDateTime(): Optional<LocalDateTime> {
        return after.map { it.atStartOfDay() }
    }

    @Hidden
    fun getBeforeAsLocalDateTime(): Optional<LocalDateTime> {
        return before.map { it.atTime(LocalTime.MAX) }
    }
}
