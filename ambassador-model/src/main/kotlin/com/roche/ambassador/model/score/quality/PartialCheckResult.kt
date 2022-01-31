package com.roche.ambassador.model.score.quality

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.score.quality.checks.Check
import kotlin.math.min

class PartialCheckResult(
    val checkName: String,
    val score: Double,
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
        private var explanation = Explanation.simple("$checkName has value $score with confidence $confidence")

        fun proportional(success: Double, total: Double, max: Double): Builder {
            return score(min(max * success / total, max))
        }

        fun score(score: Double): Builder {
            this.score = score
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
            val expBuilder = Explanation.builder()
            builder(expBuilder)
            this.explanation = expBuilder.build()
            return this
        }

        fun explanation(builder: Explanation.Builder): Builder {
            this.explanation = builder.build()
            return this
        }

        fun build(): PartialCheckResult {
            return PartialCheckResult(checkName, score, confidence, explanation)
        }
    }
}