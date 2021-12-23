package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import kotlin.reflect.KClass

class HasFeature<A : BuildableAdvice, T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val conditionsBuilder: ConditionsBuilder<A>
) : Invokable {

    private var delegate: Has<A, T> = conditionsBuilder.alwaysFalse()

    infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        delegate = conditionsBuilder.context.project.features.find(featureType)
            .map { Has(predicate, it.value().get(), conditionsBuilder) }
            .orElseGet { conditionsBuilder.alwaysFalse() }
        return delegate
    }

    infix fun thatNot(predicate: T.() -> Boolean): Has<A, T> = that(not(predicate))

    override operator fun invoke(): Boolean = delegate()
}