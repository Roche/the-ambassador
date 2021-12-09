package com.roche.ambassador.model.score

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.FileFeature
import com.roche.ambassador.model.files.File
import java.util.function.Predicate
import kotlin.reflect.KClass

typealias ScoreNormalizer = (Double) -> Double

open class ScoreBuilder<SELF : ScoreBuilder<SELF>> internal constructor(
    protected val name: String,
    private val features: Features,
    initialScore: Double,
    private val experimental: Boolean = false,
) {
    protected val usedFeatures: MutableSet<Feature<*>> = mutableSetOf()
    protected val expectedFeatures: MutableSet<KClass<*>> = mutableSetOf()
    protected val subScores: MutableSet<Score> = mutableSetOf()
    protected val explanations: MutableList<Explanation> = mutableListOf()
    protected var score: Double = initialScore
    protected val normalizers = mutableListOf<ScoreNormalizer>()

    @Suppress("UNCHECKED_CAST")
    fun <V, T : Feature<V>> withFeature(featureType: KClass<T>): FeatureScoreBuilder<V, T, SELF> {
        expectedFeatures.add(featureType)
        val feature = features.find(featureType)
        return FeatureScoreBuilder(feature.orElse(null), this as SELF)
    }

    fun addNormalizer(normalizer: ScoreNormalizer): SELF {
        normalizers.add(normalizer)
        return this as SELF
    }

    fun build(): Score {
        normalizers.forEach {
            score = it.invoke(score)
        }
        return Score.final(name, score, usedFeatures, subScores, experimental)
    }

    fun withSubScore(name: String, initialScore: Double = 0.0, experimental: Boolean = false): SubScoreBuilder {
        return SubScoreBuilder(name, this, features, initialScore, experimental)
    }

    class FeatureScoreBuilder<V, T : Feature<V>, U : ScoreBuilder<U>> internal constructor(
        private val feature: T?,
        private val scoreBuilder: U
    ) {
        private val filters = mutableListOf<Predicate<V>>()

        fun filter(predicate: Predicate<V>): FeatureScoreBuilder<V, T, U> {
            filters.add(predicate)
            return this
        }

        fun filter(predicate: (V) -> Boolean): FeatureScoreBuilder<V, T, U> {
            filters.add(predicate)
            return this
        }

        fun calculate(calculator: (V, Double) -> Double): U {
            if (feature != null && feature.exists() && filters.stream().allMatch { it.test(feature.value().get()) }) {
                scoreBuilder.usedFeatures.add(feature)
                val partialScore = calculator(feature.value().get(), scoreBuilder.score)
                scoreBuilder.score = partialScore
            }
            return scoreBuilder
        }
    }

    class ParentScoreBuilder internal constructor(
        name: String,
        features: Features,
        initialScore: Double = 0.0,
        experimental: Boolean = false,
    ) : ScoreBuilder<ParentScoreBuilder>(name, features, initialScore, experimental)

    class SubScoreBuilder internal constructor(
        name: String,
        private val scoreBuilder: ScoreBuilder<*>,
        features: Features,
        initialScore: Double = 0.0,
        experimental: Boolean,
    ) : ScoreBuilder<SubScoreBuilder>(name, features, initialScore, experimental) {

        fun reduce(reducer: (Double, Double) -> Double): ScoreBuilder<*> {
            val reduced = if (this.score.isNaN() || this.score.isInfinite()) {
                scoreBuilder.score
            } else {
                val finalScore = build()
                scoreBuilder.subScores.add(finalScore)
                reducer.invoke(scoreBuilder.score, this.score)
            }
            scoreBuilder.score = reduced
            return scoreBuilder
        }
    }
}

fun <V : File, T : FileFeature<V>> ScoreBuilder.FeatureScoreBuilder<V, T, ScoreBuilder.ParentScoreBuilder>.forFile(
    minimumSize: Long,
    boost: Int
): ScoreBuilder.ParentScoreBuilder {
    return this
        .filter { it.hasSizeAtLeast(minimumSize) }
        .calculate { _, score -> score + boost }
}
