package com.roche.ambassador.model.stats

import org.apache.commons.math3.distribution.PoissonDistribution
import java.time.Duration
import java.time.LocalDate
import kotlin.math.ceil

object TimelineGenerator {

    /*
    Generate events timeline using Poisson distribution with provided mean
     */
    fun withWeekAverage(mean: Double, weeks: Int): Timeline {
        val timeline = Timeline()
        if (mean > 0.0 && weeks > 0) {
            val poisson = PoissonDistribution(mean, weeks)
            val data = poisson.sample(weeks)
            var nextDate = LocalDate.now().minusWeeks(weeks.toLong())

            data.forEach {
                timeline.add(nextDate, it)
                nextDate = nextDate.plusWeeks(1)
            }
        }
        return timeline
    }

    fun withTotalEvents(
        count: Int,
        startDate: LocalDate = LocalDate.now().minusDays(10),
        endDate: LocalDate = LocalDate.now()
    ): Timeline {
        val timeline = Timeline()
        val inclusiveStartDate = startDate.plusDays(1)
        val days = Duration.between(inclusiveStartDate.atStartOfDay(), endDate.atStartOfDay()).toDays() + 1
        if (days == 1L) {
            timeline.add(inclusiveStartDate, count)
            return timeline
        }
        val skipEach = if (count > days) {
            1
        } else {
            ceil(days.toDouble() / count).toLong()
        }
        var currentEventsCount = 0
        var nextDate = inclusiveStartDate
        while (currentEventsCount < count) {
            timeline.add(nextDate, 1)
            currentEventsCount++
            if (nextDate.isAfter(endDate)) {
                nextDate = inclusiveStartDate
            } else {
                nextDate = nextDate.plusDays(skipEach)
            }
        }

        return timeline
    }

}