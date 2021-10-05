package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.Explanation
import com.filipowm.ambassador.model.Feature
import com.filipowm.ambassador.model.Score
import com.filipowm.ambassador.model.feature.Features
import java.util.function.Predicate
import kotlin.reflect.KClass

typealias ScoreNormalizer = (Double) -> Double

open class ScoreBuilder<SELF : ScoreBuilder<SELF>> internal constructor(protected val name: String, private val features: Features, initialScore: Double) {
    protected val usedFeatures: MutableSet<Feature<*>> = mutableSetOf()
    protected val expectedFeatures: MutableSet<KClass<*>> = mutableSetOf()
    protected val subScores: MutableSet<Score> = mutableSetOf()
    protected val explanations: MutableList<Explanation> = mutableListOf()
    protected var score: Double = initialScore
    protected val normalizers = mutableListOf<ScoreNormalizer>()

    fun <T : Feature<*>> withFeature(featureType: KClass<T>): FeatureScoreBuilder<T, SELF> {
        expectedFeatures.add(featureType)
        val feature = features.find(featureType)
        return FeatureScoreBuilder(feature.orElse(null), this as SELF)
    }

    fun addNormalizer(normalizer: ScoreNormalizer): SELF {
        normalizers.add(normalizer)
        return this as SELF
    }

    fun build(description: String): Score {
        normalizers.forEach {
            score = it.invoke(score)
        }
        return Score.final(name, description, score, usedFeatures, subScores)
    }

    fun withSubScore(name: String): SubScoreBuilder {
        return SubScoreBuilder(name, this, features)
    }

    class FeatureScoreBuilder<T : Feature<*>, U : ScoreBuilder<U>> internal constructor(
        private val feature: T?,
        private val scoreBuilder: U
    ) {
        private val filters = mutableListOf<Predicate<T>>()

        open fun filter(predicate: Predicate<T>): FeatureScoreBuilder<T, U> {
            filters.add(predicate)
            return this
        }

        open fun filter(predicate: (T) -> Boolean): FeatureScoreBuilder<T, U> {
            filters.add(predicate)
            return this
        }

        open fun calculate(calculator: (T, Double) -> Double): U {
            var partialScore: Double? = null
            if (feature != null && feature.exists() && filters.stream().allMatch { it.test(feature) }) {
                scoreBuilder.usedFeatures.add(feature)
                partialScore = calculator(feature, scoreBuilder.score)
                scoreBuilder.score = partialScore
            }
            return scoreBuilder
        }
    }

    class ParentScoreBuilder internal constructor(
        name: String,
        features: Features, initialScore: Double = 0.0
    ) : ScoreBuilder<ParentScoreBuilder>(name, features, initialScore)

    class SubScoreBuilder internal constructor(
        name: String,
        private val scoreBuilder: ScoreBuilder<*>,
        features: Features, initialScore: Double = 0.0
    ) : ScoreBuilder<SubScoreBuilder>(name, features, initialScore) {

        fun reduce(reducer: (Double, Double) -> Double): ScoreBuilder<*> {
            val finalScore = Score.final(name, "", this.score, usedFeatures, setOf())
            scoreBuilder.score = reducer.invoke(scoreBuilder.score, this.score)
            scoreBuilder.subScores.add(finalScore)
//            scoreBuilder.usedFeatures.addAll(usedFeatures)
            return scoreBuilder
        }
    }
}
