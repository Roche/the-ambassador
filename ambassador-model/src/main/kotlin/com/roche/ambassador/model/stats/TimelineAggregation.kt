package com.roche.ambassador.model.stats

import java.time.temporal.ChronoUnit

enum class TimelineAggregation(val chronoUnit: ChronoUnit) {

    WEEKLY(ChronoUnit.WEEKS),
    DAILY(ChronoUnit.DAYS),
    MONTHLY(ChronoUnit.MONTHS),
    YEARLY(ChronoUnit.YEARS),
    NONE(ChronoUnit.FOREVER);

    companion object {
        fun ofChronoUnit(chronoUnit: ChronoUnit): TimelineAggregation {
            val agg = values().firstOrNull { it.chronoUnit == chronoUnit }
            return agg ?: NONE
        }
    }
}