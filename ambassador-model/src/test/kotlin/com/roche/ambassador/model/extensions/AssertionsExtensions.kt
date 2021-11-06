package com.roche.ambassador.model.extensions

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.score.TestCalculator
import org.assertj.core.api.AbstractDoubleAssert
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.api.ObjectAssert
import kotlin.reflect.KClass

fun ObjectAssert<Score>.hasValue(expected: Double): ObjectAssert<Score> {
    extracting { it.value() }.isEqualTo(expected)
    return this
}

fun ObjectAssert<Score>.hasValueRounded(expected: Double, decimals: Int): ObjectAssert<Score> {
    extracting { it.value().round(decimals) }.isEqualTo(expected.round(decimals))
    return this
}

fun <T : Feature<*>> ObjectAssert<Score>.hasFeature(expectedFeatureType: KClass<T>): ObjectAssert<Score> {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList()
        .contains(expectedFeatureType)
    return this
}

fun <T : Feature<*>> ObjectAssert<Score>.hasOnlyFeature(expectedFeatureType: KClass<T>): ObjectAssert<Score> {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList()
        .containsExactly(expectedFeatureType)
    return this
}

fun ObjectAssert<Score>.hasFeatures(vararg expectedFeatureTypes: KClass<*>): ObjectAssert<Score> {
    extracting { it.allFeatures().map { it::class }.toMutableList() }
        .asList().containsExactlyInAnyOrder(*expectedFeatureTypes)
    return this
}

fun ObjectAssert<Score>.hasScoresSize(expected: Int): ObjectAssert<Score> {
    extracting { it.allSubScores().toMutableList() }.asList().hasSize(expected)
    return this
}

fun <T> ObjectAssert<Score>.hasCorrectValueBasedOnCalculator(data: T, calculator: TestCalculator<T>): ObjectAssert<Score> {
    val expected = calculator.calculate(data)
    extracting { it.value() }.isEqualTo(expected)
    return this
}

fun ObjectAssert<Score>.isGreaterThan(value: Double): ObjectAssert<Score> {
    withValue().isGreaterThan(value)
    return this
}

fun ObjectAssert<Score>.isLessThan(value: Double): ObjectAssert<Score> {
    withValue().isLessThan(value)
    return this
}

fun ObjectAssert<Score>.isBetween(min: Double, max: Double): ObjectAssert<Score> {
    withValue().isBetween(min, max)
    return this
}

private fun ObjectAssert<Score>.withValue(): AbstractDoubleAssert<*> {
    return extracting { it.value() }.asInstanceOf(InstanceOfAssertFactories.DOUBLE)
}
