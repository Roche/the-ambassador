package com.filipowm.ambassador.model

import com.filipowm.ambassador.model.feature.Features
import com.filipowm.ambassador.model.score.ScoreBuilder
import java.util.stream.Collectors
import java.util.stream.Stream

interface Score : Specification, Explainable {

    fun value(): Double
    fun description(): String
    fun features(): Set<Feature<*>>

    fun subScores(): Set<Score>
    fun isComposite(): Boolean = subScores().isNotEmpty()

    /**
     * Navigate through all subscores tree to extract all features
     * participating in final score
     */
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
            return composite(name, "", value, mutableSetOf(first, second))
        }

        fun builder(name: String, features: Features, initialScore: Double = 0.0): ScoreBuilder.ParentScoreBuilder = ScoreBuilder.ParentScoreBuilder(name, features, initialScore)

        internal fun composite(
            name: String,
            description: String,
            value: Double,
            subScores: MutableSet<Score>
        ): Score {
            return CompositeScore(name, description, value, subScores)
        }

        fun final(
            name: String,
            description: String,
            score: Double,
            features: Set<Feature<*>>,
            subScores: Set<Score>
        ): Score {
            return FinalScore(name, description, score, features.toSet(), subScores.toSet())
        }
    }
}

internal class CompositeScore(
    name: String, description: String,
    private val value: Double, private val subScores: MutableSet<Score>
) : AbstractScore(name, description, setOf(), subScores) {
    override fun value(): Double = value
}

internal class FinalScore(
    name: String,
    description: String,
    val score: Double,
    features: Set<Feature<*>>,
    subScores: Set<Score>
) : AbstractScore(name, description, features, subScores) {

    override fun value(): Double = score
}

abstract class AbstractScore(
    private val name: String,
    private val description: String,
    private val features: Set<Feature<*>> = mutableSetOf(),
    private val subScores: Set<Score> = mutableSetOf()
) : Score {

    override fun description() = description

    override fun features() = features

    override fun subScores() = subScores

    override fun name() = name

    override fun explain(): Explanation {
        TODO("Not yet implemented")
    }

}

