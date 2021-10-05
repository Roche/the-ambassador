package com.filipowm.ambassador.model.extensions

import com.filipowm.ambassador.model.stats.Timeline
import org.apache.commons.math3.distribution.PoissonDistribution
import java.time.LocalDate

object TimelineGenerator {

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

}