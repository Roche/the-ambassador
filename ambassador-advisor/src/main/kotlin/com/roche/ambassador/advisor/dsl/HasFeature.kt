package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import kotlin.reflect.KClass

class HasFeature<T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val rulesBuilder: RulesBuilder
) : Invokable, ThatSupport<T> {

    private var delegate: Has<T> = rulesBuilder.alwaysFalse()

    override infix fun that(predicate: T.() -> Boolean): Has<T> {
        delegate = rulesBuilder.context.project.features.find(featureType)
            .map { Has(predicate, it.value().get(), rulesBuilder) }
            .orElseGet { rulesBuilder.alwaysFalse() }
        return delegate
    }

    override operator fun invoke(): Boolean = delegate()
}
