package com.roche.ambassador.advisor.configuration

import org.springframework.util.unit.DataSize
import org.springframework.util.unit.DataSize.ofGigabytes
import org.springframework.util.unit.DataSize.ofMegabytes
import java.time.Duration
import java.time.Duration.ofDays
import java.time.Duration.ofMinutes

data class RulesProperties(
    val visibility: Rule = Rule(true),
    val description: DescriptionRule = DescriptionRule(true, 30),
    val topics: TopicsRule = TopicsRule(true, 3),
    val forking: Rule = Rule(),
    val pullRequest: PullRequestsRule = PullRequestsRule(true, PerformanceRanges(ofDays(7), ofDays(5), ofDays(2))),
    val issues: Rule = Rule(),
    val repository: RepositoryRule = RepositoryRule(true, SizeRanges(ofGigabytes(1), ofMegabytes(500), ofMegabytes(300))),
    val ci: CiRule = CiRule(
        enabled = true,
        artifactsSize = SizeRanges(ofGigabytes(1), ofMegabytes(500), ofMegabytes(300)),
        performance = CiPerformanceRule(true, PerformanceRanges(ofMinutes(30), ofMinutes(45), ofMinutes(60))),
        stability = CiStabilityRule(true)
    ),
) {

    open class Rule(val enabled: Boolean = true)

    open class Ranges<T>(val high: T, val med: T, val low: T)

    class SizeRanges(high: DataSize, med: DataSize, low: DataSize) : Ranges<DataSize>(high, med, low)

    class PerformanceRanges(high: Duration, med: Duration, low: Duration) : Ranges<Duration>(high, med, low)

    class DescriptionRule(enabled: Boolean = true, val shortLength: Int) : Rule(enabled)
    class TopicsRule(enabled: Boolean, val recommendedCount: Int) : Rule(enabled)
    class PullRequestsRule(enabled: Boolean, val closeSpeed: PerformanceRanges) : Rule(enabled)

    class CiRule(
        enabled: Boolean,
        val artifactsSize: SizeRanges,
        val performance: CiPerformanceRule,
        val stability: CiStabilityRule
    ) : Rule(enabled)

    class CiPerformanceRule(enabled: Boolean, val ranges: PerformanceRanges) : Rule(enabled)
    class CiStabilityRule(enabled: Boolean) : Rule(enabled)
    class RepositoryRule(enabled: Boolean, val size: SizeRanges) : Rule(enabled)
}
