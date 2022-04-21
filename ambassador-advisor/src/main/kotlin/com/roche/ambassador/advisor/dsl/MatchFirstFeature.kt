package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import kotlin.reflect.KClass

class MatchFirstFeature<A : BuildableAdvice, T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val rulesBuilder: RulesBuilder<A>
) : MatchFirst<A>(rulesBuilder), ThatSupport<A, T> {

    override infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        val delegate = readFeature(featureType)
            .map { Has(predicate, it, rulesBuilder) }
            .orElseGet { rulesBuilder.alwaysFalse() }
        apply(delegate)
        return delegate
    }
}
