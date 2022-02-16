package com.roche.ambassador.model.score.quality

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.score.quality.checks.Check
import com.roche.ambassador.model.score.quality.checks.Check.Companion.MAX_SCORE
import kotlin.math.min
import kotlin.math.roundToInt

class PartialCheckResult(
    val checkName: String,
    val score: Int,
    val confidence: Int,
    val explanation: Explanation
) {
    companion object {

        fun builder(checkName: String): Builder {
            return Builder(checkName)
        }

        fun empty(checkName: String): PartialCheckResult {
            return builder(checkName).build()
        }

        fun proportional(success: Double, total: Double): Double {
            return min(MAX_SCORE * success / total, MAX_SCORE)
        }

        fun proportional(success: Int, total: Int): Double {
            return proportional(success.toDouble(), total.toDouble())
        }
    }

    fun applyMetadata(checkMetadata: CheckMetadata): CheckResult {
        return CheckResult(
            checkName, checkMetadata.description,
            score, confidence,
            checkMetadata.importance, checkMetadata.attributes, explanation
        )
    }

    class Builder(val checkName: String) {
        private var score: Double = 0.0
        private var confidence = Check.MAX_CONFIDENCE
        private var explanationBuilder = Explanation.builder()
            .description("$checkName has value $score with confidence $confidence")

        fun score(score: Double): Builder {
            this.score = min(score, MAX_SCORE)
            return this
        }

        fun unsure(): Builder {
            return confidence(Check.MIN_CONFIDENCE)
        }

        fun certain(): Builder {
            return confidence(Check.MAX_CONFIDENCE)
        }

        fun confidence(confidence: Int): Builder {
            this.confidence = confidence
            return this
        }

        fun explanation(builder: (Explanation.Builder) -> Unit): Builder {
            builder(explanationBuilder)
            return this
        }

        fun explanation(builder: Explanation.Builder): Builder {
            this.explanationBuilder = builder
            return this
        }

        fun build(): PartialCheckResult {
            this.explanationBuilder.value(score)
            return PartialCheckResult(checkName, score.roundToInt(), confidence, explanationBuilder.build())
        }
    }
}