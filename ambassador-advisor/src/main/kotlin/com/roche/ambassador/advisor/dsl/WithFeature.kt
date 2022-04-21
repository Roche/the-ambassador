package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import kotlin.reflect.KClass

class WithFeature<A : BuildableAdvice, T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val rulesBuilder: RulesBuilder<A>
) : ConditionsWrapper(), ThatSupport<A, T> {

    override infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        val delegate = rulesBuilder.context.project.features.find(featureType)
            .map { Has(predicate, it.value().get(), rulesBuilder) }
            .orElseGet { rulesBuilder.alwaysFalse() }
        apply(delegate)
        return delegate
    }
}
