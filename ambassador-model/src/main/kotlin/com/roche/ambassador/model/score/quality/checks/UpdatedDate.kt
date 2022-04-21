package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.roundToHalf
import com.roche.ambassador.extensions.weeksUntilNow
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project
import java.util.*
import kotlin.math.max

internal object UpdatedDate : BaseCheck<Long>() {

    private const val POINTS_PER_MONTH = -0.5

    override fun name(): String = Check.UPDATED_PENALTY

    override fun readValue(project: Project): Long {
        return Optional.ofNullable(project.lastActivityDate)
            .map { it.weeksUntilNow() }
            .orElse(0L)
    }

    override fun calculateScore(featureValue: Long): Double {
        return max((featureValue * POINTS_PER_MONTH).roundToHalf(), -Check.MAX_SCORE)
    }

    override fun buildExplanation(featureValue: Long, score: Double, builder: Explanation.Builder) {
        builder
            .description("Last activity")
            .addDetails("$score for last activity $featureValue weeks ago.")
    }
}
