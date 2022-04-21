package com.roche.ambassador.model.stats

import com.roche.ambassador.ClockHolder
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors
import kotlin.NoSuchElementException

class Timeline(
    val series: MutableMap<LocalDate, Int>,
    val aggregation: TimelineAggregation = TimelineAggregation.NONE
) {

    constructor() : this(hashMapOf())

    fun empty(): Boolean = series.isEmpty()

    override fun toString(): String {
        return series.toString()
    }

    fun sum(): Long {
        return if (series.isEmpty()) {
            0
        } else {
            series.values.fold(0) { acc, v -> acc + v }
        }
    }

    fun average(): Double {
        val count = count()
        return if (count == 0) {
            0.0
        } else {
            sum().toDouble() / count
        }
    }

    fun count(): Int = series.size

    fun add(date: Date, value: Int): Timeline {
        val key = Instant.ofEpochMilli(date.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        series.merge(key, value, Integer::sum)
        return this
    }

    fun add(date: LocalDate, value: Int): Timeline {
        series.merge(date, value, Integer::sum)
        return this
    }

    fun add(date: LocalDateTime, value: Int): Timeline {
        series.merge(date.toLocalDate(), value, Integer::sum)
        return this
    }

    fun increment(date: Date?): Timeline {
        if (date != null) {
            return add(date, 1)
        }
        return this
    }

    fun increment(date: LocalDate?): Timeline {
        if (date != null) {
            return add(date, 1)
        }
        return this
    }

    fun increment(date: LocalDateTime?): Timeline {
        if (date != null) {
            return add(date, 1)
        }
        return this
    }

    fun by(): Aggregator {
        return Aggregator()
    }

    fun last(value: Long): LastPeriodPicker {
        return LastPeriodPicker(value)
    }

    fun movingAverage(size: Int, step: Int): List<Pair<LocalDate, Double>> {
        return series.entries
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

        private fun expand(series: MutableMap<LocalDate, Int>, chronoUnit: ChronoUnit) {
            if (series.isEmpty()) {
                return
            }
            val min = series.minOf { it.key }
            val max = series.maxOf { it.key }
            val dateRange = min..max
            val stepper = when (chronoUnit) {
                ChronoUnit.YEARS -> dateRange stepYears 1
                ChronoUnit.WEEKS -> dateRange stepWeeks 1
                ChronoUnit.DAYS -> dateRange stepDays 1
                else -> dateRange stepMonths 1
            }
            for (date in stepper) {
                series.putIfAbsent(date, 0)
            }
        }

        private fun remap(chronoUnit: ChronoUnit, keyMapper: (MutableMap.MutableEntry<LocalDate, Int>) -> (LocalDate)): Timeline {
            val new = series.entries
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
            if (!hasNext()) {
                throw NoSuchElementException("Next date would exceed max end date")
            }
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
            val boundaryDate = boundaryDateSupplier(LocalDate.now(ClockHolder.clock), last)
            val filteredseries = series.filterKeys { it.isAfter(boundaryDate) }
            return Timeline(filteredseries.toMutableMap())
        }
    }
}
