package com.filipowm.ambassador.model.extensions

import com.filipowm.ambassador.model.Feature
import com.filipowm.ambassador.model.Score
import org.assertj.core.api.ObjectAssert
import kotlin.reflect.KClass

fun ObjectAssert<Score>.hasValue(expected: Double): ObjectAssert<Score> {
    extracting { it.value() }.isEqualTo(expected)
    return this
}

fun <T : Feature<*>> ObjectAssert<Score>.hasFeature(expectedFeatureType: KClass<T>): ObjectAssert<Score> {
    extracting { it.allFeatures().map { it::class } }
        .matches({ it.contains(expectedFeatureType) }, "Score is not built from expected feature $expectedFeatureType")
    return this
}

fun ObjectAssert<Score>.hasScoresSize(expected: Int): ObjectAssert<Score> {
    extracting { it.allSubScores() }.matches({ it.size == expected }, "Score is not built from $expected subScores")
    return this
}


