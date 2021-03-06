package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.monthsUntilNow
import com.roche.ambassador.extensions.roundToHalf
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project
import kotlin.math.min

internal object CreatedDate : BaseCheck<Long>() {

    private const val POINTS_PER_MONTH = 0.5

    override fun name(): String = Check.CREATED_BOOST

    override fun readValue(project: Project): Long {
        return project.createdDate.monthsUntilNow()
    }

    override fun calculateScore(featureValue: Long): Double {
        return min((featureValue * POINTS_PER_MONTH).roundToHalf(), 10.0)
    }

    override fun buildExplanation(featureValue: Long, score: Double, builder: Explanation.Builder) {
        builder
            .description("Creation date")
            .addDetails("$score for project created $featureValue months ago.")
    }
}
