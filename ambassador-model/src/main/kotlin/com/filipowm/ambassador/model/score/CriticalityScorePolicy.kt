package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.project.Project
import java.time.LocalDate
import java.time.Period
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.roundToInt

object CriticalityScorePolicy : ScorePolicy<Double> {

    enum class CriticalityCheck(val weight: Double, val threshold: Int, val check: (Project) -> Number) {
        CREATED_SINCE(1.0, 60, { Period.between(it.createdDate, LocalDate.now()).months }),
        LAST_UPDATED(-1.0, 60, { Period.between(it.lastUpdatedDate, LocalDate.now()).months }),
        CONTRIBUTORS_COUNT(2.0, 5000, { 1 /* it.contributors.count */ }),
        ORGANIZATIONS_COUNT(1.0, 10, { 1 /* it.organizations.count */ }), // TODO
//        COMMIT_FREQUENCY(1.0, 1000, { p -> withNotNull(p.commits) { it.last(1).years().by().weeks().average() } }),
//        RECENT_RELEASES_COUNT(.5, 24, { p -> withNotNull(p.releases) { it.last(1).years().count().toDouble() } }),
//        CLOSED_ISSUES_COUNT(.5, 5000, { p -> withNotNull(p.issues) { it.closedIn90Days.toDouble() } }),
//        OPENED_ISSUES_COUNT(.5, 5000, { p -> withNotNull(p.issues) { it.openedIn90Days.toDouble() } }),
        COMMIT_FREQUENCY(1.0, 1000, { 1 }),
        RECENT_RELEASES_COUNT(.5, 24, { 1 }),
        CLOSED_ISSUES_COUNT(.5, 5000, { 1 }),
        OPENED_ISSUES_COUNT(.5, 5000, { 1 }),
        COMMENT_FREQUENCY(1.0, 15, { 1 /* it.comments.in90Days*/ }), // TODO
        DEPENDENTS_COUNT(2.0, 500000, { 1 /* it.dependents.count */ }) // TODO
        ;

        fun calc(project: Project): Double {
            val value = check(project).toDouble()
            return weight * log10(1 + value) / log10(1 + max(value, threshold.toDouble()))
        }

        companion object {
            fun sumWeights(): Double {
                return values()
                    .map { it.weight }
                    .reduce { acc, weight -> acc + weight }
            }
        }
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }

    override fun calculateScoreOf(project: Project): Double {
        val criticalitySum = CriticalityCheck.values()
            .map { it.calc(project) }
            .reduce { acc, result -> acc + result }

        return (criticalitySum / CriticalityCheck.sumWeights()).round(4)
    }
}
