package com.roche.ambassador.model.extensions

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.score.TestCalculator
import org.assertj.core.api.AbstractDoubleAssert
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.api.ObjectAssert
import kotlin.reflect.KClass

typealias ScoreAssert = ObjectAssert<Score>

fun ScoreAssert.hasValue(expected: Double): ScoreAssert {
    extracting { it.value() }.isEqualTo(expected)
    return this
}

fun ScoreAssert.hasValueRounded(expected: Double, decimals: Int): ScoreAssert {
    extracting { it.value().round(decimals) }.isEqualTo(expected.round(decimals))
    return this
}

fun <T : Feature<*>> ScoreAssert.hasFeature(expectedFeatureType: KClass<T>): ScoreAssert {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList()
        .contains(expectedFeatureType)
    return this
}

fun <T : Feature<*>> ScoreAssert.hasOnlyFeature(expectedFeatureType: KClass<T>): ScoreAssert {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList()
        .containsExactly(expectedFeatureType)
    return this
}

fun ScoreAssert.hasFeatures(vararg expectedFeatureTypes: KClass<*>): ScoreAssert {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList().containsExactlyInAnyOrder(*expectedFeatureTypes)
    return this
}

fun ScoreAssert.hasScoresSize(expected: Int): ScoreAssert {
    extracting { it.allSubScores().toMutableList() }.asList().hasSize(expected)
    return this
}

fun <T> ScoreAssert.hasCorrectValueBasedOnCalculator(data: T, calculator: TestCalculator<T>): ScoreAssert {
    val expected = calculator.calculate(data)
    extracting { it.value() }.isEqualTo(expected)
    return this
}

fun ScoreAssert.isGreaterThan(value: Double): ScoreAssert {
    withValue().isGreaterThan(value)
    return this
}

fun ScoreAssert.isLessThan(value: Double): ScoreAssert {
    withValue().isLessThan(value)
    return this
}

fun ScoreAssert.isBetween(min: Double, max: Double): ScoreAssert {
    withValue().isBetween(min, max)
    return this
}

private fun ScoreAssert.withValue(): AbstractDoubleAssert<*> {
    return extracting { it.value() }.asInstanceOf(InstanceOfAssertFactories.DOUBLE)
}
