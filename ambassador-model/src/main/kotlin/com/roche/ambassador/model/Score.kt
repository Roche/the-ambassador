package com.roche.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
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

        fun zip(name: String, first: Score, second: Score, zipper: (Double, Double) -> Double): Score {
            val value = zipper.invoke(first.value(), second.value())
            return composite(name, value, mutableSetOf(first, second))
        }

        fun builder(name: String, features: Features, initialScore: Double = 0.0): ScoreBuilder.ParentScoreBuilder = ScoreBuilder.ParentScoreBuilder(name, features, initialScore)

        internal fun composite(
            name: String,
            value: Double,
            subScores: MutableSet<Score>
        ): Score {
            return CompositeScore(name, value, subScores)
        }

        fun final(
            name: String,
            value: Double,
            features: Set<Feature<*>>,
            subScores: Set<Score>
        ): Score = finalWithNames(name, value, features, features.map { it.name() }.toSet(), subScores)

        fun finalWithNames(
            name: String,
            value: Double,
            features: Set<Feature<*>>,
            featureNames: Set<String>,
            subScores: Set<Score>
        ): Score {
            return FinalScore(name, value, features.toSet(), featureNames, subScores)
        }
    }
}

@JsonPropertyOrder("name", "value")
internal data class CompositeScore(
    val name: String,
    val value: Double,
    val subScores: MutableSet<Score>
) : AbstractScore(name, setOf(), subScores) {
    override fun value(): Double = value
}

@JsonPropertyOrder("name", "value")
internal data class FinalScore(
    val name: String,
    val value: Double,
    @JsonIgnore
    val features: Set<Feature<*>>,
    @JsonProperty("features")
    val featureNames: Set<String>,
    val subScores: Set<Score>,
) : AbstractScore(name, features, subScores) {

    override fun value(): Double = value
}

abstract class AbstractScore(
    private val name: String,
    @JsonIgnore
    private val features: Set<Feature<*>> = mutableSetOf(),
    private val subScores: Set<Score> = mutableSetOf()
) : Score {

    @JsonIgnore
    override fun features() = features

    override fun subScores() = subScores

    override fun name() = name

    fun getSubScoreByName(name: String): Optional<Score> {
        return Optional.ofNullable(subScores.firstOrNull { it.name().equals(name, ignoreCase = true) })
    }

    fun getSubScoreValueByNameOrZero(name: String): Double {
        return getSubScoreByName(name)
            .map { it.value() }
            .orElseGet { 0.0 }
    }

    @JsonIgnore
    override fun explain(): Explanation {
        TODO("Not yet implemented")
    }
}
