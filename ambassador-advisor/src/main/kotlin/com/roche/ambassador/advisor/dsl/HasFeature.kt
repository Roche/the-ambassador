package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import kotlin.reflect.KClass

class HasFeature<A : BuildableAdvice, T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val rulesBuilder: RulesBuilder<A>
) : Invokable, ThatSupport<A, T> {

    private var delegate: Has<A, T> = rulesBuilder.alwaysFalse()

    override infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        delegate = rulesBuilder.context.project.features.find(featureType)
            .map { Has(predicate, it.value().get(), rulesBuilder) }
            .orElseGet { rulesBuilder.alwaysFalse() }
        return delegate
    }

    override operator fun invoke(): Boolean = delegate()
}
