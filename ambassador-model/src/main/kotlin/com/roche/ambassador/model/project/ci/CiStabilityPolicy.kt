package com.roche.ambassador.model.project.ci

import com.roche.ambassador.extensions.asPercentageString
import com.roche.ambassador.extensions.round

object CiStabilityPolicy {

    fun calculateStability(
        executions: List<CiExecution>,
        configuration: CiStabilityConfiguration
    ): CiStability {
        val successes = executions.count { it.state == CiExecution.State.SUCCESS }
        val failures = executions.count { it.state == CiExecution.State.FAILURE }
        val total = successes + failures
        val stabilityLevel = if (total > 0) {
            (successes.toDouble() / total).round(2)
        } else {
            0.0
        }
        val stabilityLevelPercentage = stabilityLevel.asPercentageString()
        val state: CiStability.State = when {
            stabilityLevel > configuration.stableThreshold -> CiStability.State.STABLE
            stabilityLevel > configuration.occasionallyFailingThreshold -> CiStability.State.OCCASIONALLY_FAILING
            stabilityLevel > configuration.unstableThreshold -> CiStability.State.UNSTABLE
            else -> CiStability.State.CRITICALLY_UNSTABLE
        }
        return CiStability(stabilityLevel, stabilityLevelPercentage, failures, successes, total, state)
    }
}
