package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.extensions.round
import com.filipowm.ambassador.model.IndexEntry
import com.filipowm.ambassador.model.stats.Timeline

open class TimelineFeature(value: Timeline? = Timeline(), name: String) : AbstractFeature<Timeline>(value, name) {
    override fun asIndexEntry(): IndexEntry {
        val timeline = value.orElse { Timeline() }
        if (timeline.empty()) {
            return IndexEntry.no()
        }
        val series = timeline.series()
        return IndexEntry.of(
            name, mapOf(
                "aggregated" to timeline.aggregation.name,
                "periods" to timeline.count(),
                "sum" to timeline.sum(),
                "mean" to timeline.average().round(2),
                "startDate" to series.first().date,
                "endDate" to series.last().date
            )
        )
    }

}