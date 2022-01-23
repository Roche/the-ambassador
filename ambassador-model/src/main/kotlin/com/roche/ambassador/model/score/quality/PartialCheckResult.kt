package com.roche.ambassador.model.score.quality

import com.roche.ambassador.model.score.quality.checks.Check
import kotlin.math.min

class PartialCheckResult(
    val checkName: String,
    val score: Double,
    val confidence: Int,
    val reason: String
) {
    companion object {
        fun noResult(checkName: String, reason: String): PartialCheckResult {
            return certain(checkName, 0.0, reason)
        }

        fun certain(checkName: String, score: Double, reason: String): PartialCheckResult {
            return withConfidence(checkName, score, Check.MAX_CONFIDENCE, reason)
        }

        fun unsure(checkName: String, score: Double, reason: String): PartialCheckResult {
            return withConfidence(checkName, score, Check.MIN_CONFIDENCE, reason)
        }

        fun withConfidence(checkName: String, score: Double, confidence: Int, reason: String): PartialCheckResult {
            return PartialCheckResult(checkName, score, confidence, reason)
        }

        fun proportional(checkName: String, success: Double, total: Double, max: Double, reason: String, confidence: Int = Check.MAX_CONFIDENCE): PartialCheckResult {
            val score = min(max * success / total, max)
            return withConfidence(checkName, score, confidence, reason)
        }
    }

    fun applyMetadata(checkMetadata: CheckMetadata): CheckResult {
        return CheckResult(
            checkName, checkMetadata.description,
            score, confidence,
            reason, checkMetadata.importance, checkMetadata.attributes
        )
    }
}