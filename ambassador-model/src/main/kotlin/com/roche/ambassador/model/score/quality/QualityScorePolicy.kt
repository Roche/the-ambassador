package com.roche.ambassador.model.score.quality

import com.roche.ambassador.extensions.roundToHalf
import com.roche.ambassador.extensions.toPrettyString
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.score.ScorePolicy
import com.roche.ambassador.model.score.quality.checks.Check

class QualityScorePolicy(
    private val checks: List<Check>,
    private val metadata: Map<String, CheckMetadata>,
    private val experimental: Boolean
) : ScorePolicy {

    override fun calculateScoreOf(features: Features): Score {
        val scores = checks.map { it.check(features) }
            .map { resolveFinalResult(it) }
        val scoresPerQualityAttribute = QualityAttribute.values().map { it to ScoreValueHolder() }.toMap().toMutableMap()
        val explanations: MutableList<Explanation> = mutableListOf()
        for (score in scores) {
            score.attributes
                .forEach {
                    scoresPerQualityAttribute[it.key]!!.add(score.score, it.value)
                }
            explanations += score.explanation
        }
        val builder = Score.builder("Quality", features, experimental, 0.0)
            .addExplanations(explanations)
            .addNormalizer { (it / scoresPerQualityAttribute.size).roundToHalf() }
        for (qualityAttributeScore in scoresPerQualityAttribute) {
            builder.withSubScore(qualityAttributeScore.key.toPrettyString(), qualityAttributeScore.value.get(), experimental)
                .reduce { total, attribute -> total + attribute }
        }
        return builder.build()
    }

    private fun resolveFinalResult(partialResult: PartialCheckResult): CheckResult {
        val metadata = metadata[partialResult.checkName]!!
        return partialResult.applyMetadata(metadata)
    }

    private class ScoreValueHolder {
        private var partialsCount: Int = 0
        private var score: Double = 0.0
        private var weights: Double = 0.0
        fun add(score: Int, weight: Double) {
            this.score += (score * weight)
            this.weights += weight
            partialsCount++
        }

        fun get(): Double = (score / weights).roundToHalf()
    }
}
