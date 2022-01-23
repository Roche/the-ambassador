package com.roche.ambassador.model.score.quality

import com.roche.ambassador.extensions.roundToHalf
import com.roche.ambassador.extensions.toPrettyString
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.score.ScorePolicy
import com.roche.ambassador.model.score.quality.checks.Check

class QualityScorePolicy(
    private val checks: List<Check>,
    private val metadata: Map<String, CheckMetadata>,
    private val experimental: Boolean
) : ScorePolicy {

    override fun calculateScoreOf(project: Project): Score {
        val scores = checks.map { it.check(project) }
            .map { resolveFinalResult(it) }
        val scoresPerQualityAttribute = QualityAttribute.values().map { it to 1.0 }.toMap().toMutableMap()
        for (score in scores) {
            score.attributes
                .map { it.key to it.value * score.score }
                .forEach {
                    scoresPerQualityAttribute[it.first] = scoresPerQualityAttribute[it.first]!! + it.second
                }
        }
        val builder = Score.builder("Quality", project.features, experimental, 0.0)
        for (qualityAttributeScore in scoresPerQualityAttribute) {
            builder.withSubScore(qualityAttributeScore.key.toPrettyString(), qualityAttributeScore.value.roundToHalf() - 1, experimental)
                .reduce { total, attribute -> total + attribute }
        }
        return builder.build()
    }

    private fun resolveFinalResult(partialResult: PartialCheckResult): CheckResult {
        val metadata = metadata[partialResult.checkName]!!
        return partialResult.applyMetadata(metadata)
    }
}