package com.roche.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.score.ScoreBuilder
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

interface Score : Specification, Explainable {

    fun value(): Double
    fun features(): Set<Feature<*>>
    fun subScores(): Set<Score>

    fun isExperimental(): Boolean = false

    @JsonIgnore
    fun isComposite(): Boolean = subScores().isNotEmpty()

    /**
     * Navigate through all subscores tree to extract all features
     * participating in final score
     */
    @JsonIgnore
    fun allFeatures(): Set<Feature<*>> {
        return Stream.concat(
            features().stream(),
            subScores()
                .stream()
                .flatMap { it.allFeatures().stream() }
        ).collect(Collectors.toUnmodifiableSet())
    }

    /**
     * Navigate through all subscores tree to extract all scores
     * participating in final score
     */
    @JsonIgnore
    fun allSubScores(): Set<Score> {
        return Stream.concat(
            subScores().stream(),
            subScores()
                .stream()
                .flatMap { it.allSubScores().stream() }
        ).collect(Collectors.toUnmodifiableSet())
    }

    companion object {

        fun zip(name: String, first: Score, second: Score, zipper: (Double, Double, Explanation.Builder) -> Double): Score {
            val explanationBuilder = Explanation.builder()
            val value = zipper.invoke(first.value(), second.value(), explanationBuilder)
            val explanation = explanationBuilder.build()
            return composite(name, value, mutableSetOf(first, second), first.isExperimental() || second.isExperimental(), explanation)
        }

        fun builder(name: String, features: Features, experimental: Boolean = false, initialScore: Double = 0.0): ScoreBuilder.ParentScoreBuilder {
            return ScoreBuilder.ParentScoreBuilder(name, features, initialScore, experimental)
        }

        internal fun composite(
            name: String,
            value: Double,
            subScores: MutableSet<Score>,
            experimental: Boolean,
            explanation: Explanation? = null
        ): Score {
            return CompositeScore(name, value, subScores, experimental, explanation)
        }

        fun final(
            name: String,
            value: Double,
            features: Set<Feature<*>>,
            subScores: Set<Score>,
            experimental: Boolean,
            explanation: Explanation? = null
        ): Score = finalWithNames(name, value, features, features.map { it.name() }.toSet(), subScores, experimental, explanation)

        fun finalWithNames(
            name: String,
            value: Double,
            features: Set<Feature<*>>,
            featureNames: Set<String>,
            subScores: Set<Score>,
            experimental: Boolean,
            explanation: Explanation? = null
        ): Score {
            return FinalScore(name, value, features.toSet(), featureNames, subScores, experimental, explanation)
        }
    }
}

@JsonPropertyOrder("name", "value")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
internal data class CompositeScore(
    val name: String,
    val value: Double,
    val subScores: MutableSet<Score>,
    val experimental: Boolean = false,
    val explanation: Explanation? = null
) : AbstractScore(name, setOf(), subScores, experimental, explanation) {
    override fun value(): Double = value
}

@JsonPropertyOrder("name", "value")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
internal data class FinalScore(
    val name: String,
    val value: Double,
    @JsonIgnore
    val features: Set<Feature<*>>,
    @JsonProperty("features")
    val featureNames: Set<String>,
    val subScores: Set<Score>,
    val experimental: Boolean = false,
    val explanation: Explanation? = null
) : AbstractScore(name, features, subScores, experimental, explanation) {

    override fun value(): Double = value
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
abstract class AbstractScore(
    private val name: String,
    @JsonIgnore
    private val features: Set<Feature<*>> = setOf(),
    private val subScores: Set<Score> = setOf(),
    private val experimental: Boolean = false,
    private val explanation: Explanation ? = null
) : Score {

    @JsonIgnore
    override fun features() = features

    override fun subScores() = subScores

    override fun name() = name

    override fun explain(): Explanation = explanation ?: Explanation.empty()

    override fun isExperimental(): Boolean = experimental

    fun getSubScoreByName(name: String): Optional<Score> {
        return Optional.ofNullable(subScores.firstOrNull { it.name().equals(name, ignoreCase = true) })
    }

    fun getSubScoreValueByNameOrZero(name: String): Double {
        return getSubScoreByName(name)
            .map { it.value() }
            .orElseGet { 0.0 }
    }
}
