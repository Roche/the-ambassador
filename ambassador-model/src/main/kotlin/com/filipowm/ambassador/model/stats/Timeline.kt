package com.filipowm.ambassador.model.stats

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

class Timeline(@JsonIgnore private val data: MutableMap<LocalDate, Int>, val aggregation: TimelineAggregation = TimelineAggregation.NONE) {

    constructor() : this(hashMapOf())

    fun empty(): Boolean = data.isEmpty()

    @JsonProperty("series")
    fun series(): List<Point> = data.toSortedMap()
        .toList()
        .map { Point(it.first, it.second) }

    override fun toString(): String {
        return data.toString()
    }

    fun sum(): Long {
        return data.values.fold(0) { acc, v -> acc + v }
    }

    fun average(): Double {
        if (count() == 0) {
            return 0.0
        }
        return sum().toDouble() / count()
    }

    fun count(): Int {
        return data.size
    }

    fun add(date: Date, value: Int): Timeline {
        val key = Instant.ofEpochMilli(date.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        data.merge(key, value, Integer::sum)
        return this
    }

    fun add(date: LocalDate, value: Int): Timeline {
        data.merge(date, value, Integer::sum)
        return this
    }

    fun add(date: LocalDateTime, value: Int): Timeline {
        data.merge(date.toLocalDate(), value, Integer::sum)
        return this
    }

    fun increment(date: Date): Timeline {
        return add(date, 1)
    }

    fun increment(date: LocalDate): Timeline {
        return add(date, 1)
    }

    fun increment(date: LocalDateTime): Timeline {
        return add(date, 1)
    }

    fun by(): Aggregator {
        return Aggregator()
    }

    fun last(value: Long): LastPeriodPicker {
        return LastPeriodPicker(value)
    }

    fun movingAverage(size: Int, step: Int): List<Pair<LocalDate, Double>> {
        return data.entries
            .sortedBy { it.key }
            .windowed(size, step)
            .map { x -> Pair(x.first().key, x.sumOf { it.value }.toDouble() / x.size) }
    }

    inner class Aggregator {
        fun days(): Timeline {
            return remap(ChronoUnit.DAYS) { it.key }
        }

        fun months(): Timeline {
            return remap(ChronoUnit.MONTHS) { it.key.withDayOfMonth(1) }
        }

        fun weeks(): Timeline {
            return remap(ChronoUnit.WEEKS) { it.key.with(DayOfWeek.MONDAY) }
        }

        fun years(): Timeline {
            return remap(ChronoUnit.YEARS) { it.key.withDayOfYear(1) }
        }

        private fun expand(data: MutableMap<LocalDate, Int>, chronoUnit: ChronoUnit) {
            if (data.isEmpty()) {
                return
            }
            val min = data.minOf { it.key }
            val max = data.maxOf { it.key }
            val dateRange = min..max
            val stepper = when (chronoUnit) {
                ChronoUnit.YEARS -> dateRange stepYears 1
                ChronoUnit.WEEKS -> dateRange stepWeeks 1
                ChronoUnit.DAYS -> dateRange stepDays 1
                else -> dateRange stepMonths 1
            }
            for (date in stepper) {
                data.putIfAbsent(date, 0)
            }
        }

        private fun remap(chronoUnit: ChronoUnit, keyMapper: (MutableMap.MutableEntry<LocalDate, Int>) -> (LocalDate)): Timeline {
            val new = data.entries
                .stream()
                .collect(
                    Collectors.toMap(
                        keyMapper,
                        { it.value },
                        Integer::sum
                    )
                )
            expand(new, chronoUnit)
            return Timeline(new, TimelineAggregation.ofChronoUnit(chronoUnit))
        }
    }

    class DateIterator(
        startDate: LocalDate,
        val endDateInclusive: LocalDate,
        val step: Long,
        val stepUnit: ChronoUnit = ChronoUnit.MONTHS
    ) : Iterator<LocalDate> {
        private var currentDate = startDate

        override fun hasNext(): Boolean = currentDate <= endDateInclusive

        override fun next(): LocalDate {

            val next = currentDate
            currentDate = currentDate.plus(step, stepUnit)

            return next
        }
    }

    class DateProgression(
        override val start: LocalDate,
        override val endInclusive: LocalDate,
        val step: Long = 1,
        val stepUnit: ChronoUnit = ChronoUnit.MONTHS
    ) :
        Iterable<LocalDate>, ClosedRange<LocalDate> {

        override fun iterator(): Iterator<LocalDate> =
            DateIterator(start, endInclusive, step, stepUnit)

        fun step(step: Long, chronoUnit: ChronoUnit): DateProgression = DateProgression(start, endInclusive, step, chronoUnit)

        infix fun stepDays(step: Long): DateProgression = step(step, ChronoUnit.DAYS)
        infix fun stepMonths(step: Long): DateProgression = step(step, ChronoUnit.MONTHS)
        infix fun stepWeeks(step: Long): DateProgression = step(step, ChronoUnit.WEEKS)
        infix fun stepYears(step: Long): DateProgression = step(step, ChronoUnit.YEARS)
    }

    operator fun LocalDate.rangeTo(other: LocalDate): DateProgression = DateProgression(this, other)

    inner class LastPeriodPicker(private val last: Long) {
        fun days(): Timeline {
            return filtered(LocalDate::minusDays)
        }

        fun months(): Timeline {
            return filtered(LocalDate::minusMonths)
        }

        fun years(): Timeline {
            return filtered(LocalDate::minusYears)
        }

        private fun filtered(boundaryDateSupplier: (LocalDate, Long) -> LocalDate): Timeline {
            val boundaryDate = boundaryDateSupplier(LocalDate.now(), last)
            val filteredData = data.filterKeys { it.isAfter(boundaryDate) }
            return Timeline(filteredData.toMutableMap())
        }
    }

    data class Point(val date: LocalDate, val count: Int)
}
