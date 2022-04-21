package com.roche.ambassador.model.score

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.FileFeature
import com.roche.ambassador.model.files.File
import com.roche.ambassador.model.project.Project
import java.util.function.Predicate
import kotlin.reflect.KClass

typealias ScoreNormalizer = (Double) -> Double

open class ScoreBuilder<SELF : ScoreBuilder<SELF>> internal constructor(
    protected val name: String,
    private val project: Project,
    initialScore: Double,
    private val experimental: Boolean = false,
) {
    protected val usedFeatures: MutableSet<Feature<*>> = mutableSetOf()
    protected val expectedFeatures: MutableSet<KClass<*>> = mutableSetOf()
    protected val subScores: MutableSet<Score> = mutableSetOf()
    protected val explanations: MutableList<Explanation> = mutableListOf()
    protected val reasons: MutableList<String> = mutableListOf()
    protected var score: Double = initialScore
    protected val normalizers = mutableListOf<ScoreNormalizer>()

    @Suppress("UNCHECKED_CAST")
    fun <V, T : Feature<V>> withFeature(featureType: KClass<T>): FeatureScoreBuilder<V, T, SELF> {
        expectedFeatures.add(featureType)
        val feature = project.features.find(featureType)
        return FeatureScoreBuilder(feature.orElse(null), this as SELF)
    }

    fun addExplanations(vararg reasons: Explanation): SELF {
        this.explanations += reasons
        return this as SELF
    }

    fun addReasons(vararg reasons: String): SELF {
        this.reasons += reasons
        return this as SELF
    }

    fun addExplanations(reasons: List<Explanation>): SELF {
        this.explanations += reasons
        return this as SELF
    }

    fun addNormalizer(normalizer: ScoreNormalizer): SELF {
        normalizers.add(normalizer)
        return this as SELF
    }

    fun build(): Score {
        normalizers.forEach {
            score = it.invoke(score)
        }
        val explanation = if (explanations.isNotEmpty() || reasons.isNotEmpty()) {
            Explanation(children = explanations, details = reasons)
        } else {
            null
        }
        return Score.final(name, score, usedFeatures, subScores, experimental, explanation)
    }

    fun withSubScore(name: String, initialScore: Double = 0.0, experimental: Boolean = false): SubScoreBuilder {
        return SubScoreBuilder(name, this, project, initialScore, experimental)
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

        fun sum(partialCalculator: (V) -> Double, explainer: Explainer<V>? = null): U {
            apply(partialCalculator, { partial, agg -> agg + partial }, explainer)
            return scoreBuilder
        }

        fun multiply(partialCalculator: (V) -> Double, explainer: Explainer<V>? = null): U {
            apply(partialCalculator, { partial, agg -> agg * partial }, explainer)
            return scoreBuilder
        }

        fun sum(partialCalculator: (V) -> Double): U {
            return sum(partialCalculator, null)
        }

        fun multiply(partialCalculator: (V) -> Double): U {
            return multiply(partialCalculator, null)
        }

        private fun apply(
            part: Double?,
            aggregator: (Double, Double) -> Double,
            explainer: Explainer<V>? = null
        ) {
            if (part != null) {
                val aggregated = aggregator(part, scoreBuilder.score)
                scoreBuilder.usedFeatures.add(feature!!)
                scoreBuilder.score = aggregated
                if (explainer != null) {
                    scoreBuilder.addReasons(explainer(feature.value().get(), part.round(2)))
                }
            }
        }

        private fun apply(
            partialCalculator: (V) -> Double,
            aggregator: (Double, Double) -> Double,
            explainer: Explainer<V>? = null
        ) {
            val part = calcPartial(partialCalculator)
            return apply(part, aggregator, explainer)
        }

        private fun calcPartial(partialCalculator: (V) -> Double): Double? {
            return if (feature != null && feature.exists() && filters.stream().allMatch { it.test(feature.value().get()) }) {
                partialCalculator(feature.value().get())
            } else {
                null
            }
        }

        fun calculate(
            calculator: (V, Double) -> Double,
            explainer: Explainer<V>? = null
        ): U {
            if (feature != null && feature.exists() && filters.stream().allMatch { it.test(feature.value().get()) }) {
                val partialScore = calculator(feature.value().get(), scoreBuilder.score)
                apply(partialScore, { _, _ -> partialScore }, explainer)
            }
            return scoreBuilder
        }

        fun calculate(calculator: (V, Double) -> Double): U {
            return calculate(calculator, null)
        }
    }

    class ParentScoreBuilder internal constructor(
        name: String,
        project: Project,
        initialScore: Double = 0.0,
        experimental: Boolean = false,
    ) : ScoreBuilder<ParentScoreBuilder>(name, project, initialScore, experimental)

    class SubScoreBuilder internal constructor(
        name: String,
        private val scoreBuilder: ScoreBuilder<*>,
        project: Project,
        initialScore: Double = 0.0,
        experimental: Boolean,
    ) : ScoreBuilder<SubScoreBuilder>(name, project, initialScore, experimental) {

        private var reasonProvider: ((Double, Double) -> String)? = null

        fun withReason(reasonProvider: (Double, Double) -> String): SubScoreBuilder {
            this.reasonProvider = reasonProvider
            return this
        }

        fun reduce(reducer: (Double, Double) -> Double): ScoreBuilder<*> {
            val reduced = if (this.score.isNaN() || this.score.isInfinite()) {
                scoreBuilder.score
            } else {
                val finalScore = build()
                scoreBuilder.subScores.add(finalScore)
                reducer.invoke(scoreBuilder.score, this.score)
            }
            scoreBuilder.score = reduced
            if (reasonProvider != null) {
                val reason = reasonProvider!!.invoke(this.score, reduced.round(2))
                scoreBuilder.addReasons(reason)
            }
            return scoreBuilder
        }
    }
}

fun <V : File, T : FileFeature<V>> ScoreBuilder.FeatureScoreBuilder<V, T, ScoreBuilder.ParentScoreBuilder>.forFile(
    minimumSize: Long,
    boost: Int
): ScoreBuilder.ParentScoreBuilder {
    return forFile(minimumSize, boost, null)
}

fun <V : File, T : FileFeature<V>> ScoreBuilder.FeatureScoreBuilder<V, T, ScoreBuilder.ParentScoreBuilder>.forFile(
    minimumSize: Long,
    boost: Int,
    explainer: Explainer<V>?
): ScoreBuilder.ParentScoreBuilder {
    return this
        .filter { it.hasSizeAtLeast(minimumSize) }
        .sum({ boost.toDouble() }, explainer)
}

typealias Explainer<V> = (V, Double) -> String
