package com.roche.ambassador.model.feature

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.IndexEntry
import com.roche.ambassador.model.stats.Timeline
import com.roche.ambassador.model.stats.TimelineAggregation

open class TimelineFeature(value: Timeline? = Timeline(), name: String) : AbstractFeature<Timeline>(value, name) {
    override fun asIndexEntry(): IndexEntry {
        val timeline = value.orElse { Timeline() }
        if (timeline.empty()) {
            return IndexEntry.no()
        }
        val aggregatedTimeline = if (timeline.aggregation == TimelineAggregation.NONE) {
            timeline.by().weeks()
        } else {
            timeline
        }
        val series = aggregatedTimeline.series()
        return IndexEntry.of(
            name, mapOf(
                "aggregated" to aggregatedTimeline.aggregation.name,
                "periods" to aggregatedTimeline.count(),
                "sum" to aggregatedTimeline.sum(),
                "mean" to aggregatedTimeline.average().round(2),
                "startDate" to series.first().date,
                "endDate" to series.last().date
            )
        )
    }
}