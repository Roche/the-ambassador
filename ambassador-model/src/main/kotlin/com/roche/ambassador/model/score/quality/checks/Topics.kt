package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project
import kotlin.math.min

internal object Topics : BaseCheck<Int>() {

    private const val TOPIC_SCORE: Int = 3

    override fun name(): String = Check.TOPICS

    override fun readValue(project: Project): Int {
        return project.topics.size
    }

    override fun calculateScore(featureValue: Int): Double {
        return min(featureValue * TOPIC_SCORE, 10).toDouble()
    }

    override fun buildExplanation(featureValue: Int, score: Double, builder: Explanation.Builder) {
        builder
            .description("Topics (labels)")
            .addDetails("$score for $featureValue topics (${TOPIC_SCORE}pt each)")
    }
}
