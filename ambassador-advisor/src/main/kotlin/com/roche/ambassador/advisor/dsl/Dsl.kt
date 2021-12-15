package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.model.BuildableAdvice
import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.project.Project
import kotlin.reflect.KClass

object Dsl {

    fun <A : BuildableAdvice> advise(buildableAdvice: A, context: AdvisorContext, handler: AdviceBuilder<A>.() -> Unit) {
        val builder = AdviceBuilder(buildableAdvice, context)
        handler(builder)
    }

}

class Has<A : BuildableAdvice, T> internal constructor(
    private val predicate: T.() -> Boolean,
    private val testValue: T?,
    private val adviceBuilder: AdviceBuilder<A>
) {

    infix fun and(andPredicate: T.() -> Boolean): Has<A, T> = Has({ predicate(this) && andPredicate(this) }, testValue, adviceBuilder)

    infix fun then(adviceKey: String) {
        then {
            val config = it.getAdviceConfig(adviceKey)
            adviceBuilder.buildableAdvice.apply(config)
        }
    }

    infix fun then(action: (AdvisorContext) -> Unit) {
        if (testValue != null && predicate(testValue)) {
            action(adviceBuilder.context)
        }
    }
}

class HasFeature<A : BuildableAdvice, T, F : Feature<T>>(
    private val featureType: KClass<F>,
    private val adviceBuilder: AdviceBuilder<A>
) {

    infix fun that(predicate: T.() -> Boolean): Has<A, T> {
        return adviceBuilder.context.project.features.find(featureType)
            .map { Has(predicate, it.value().get(), adviceBuilder) }
            .orElseGet { Has({ false }, null, adviceBuilder) }
    }

    infix fun thatNot(predicate: T.() -> Boolean): Has<A, T> = that { !predicate(this) }
}

class AdviceBuilder<A : BuildableAdvice> internal constructor(val buildableAdvice: A, val context: AdvisorContext) {

    infix fun has(predicate: Project.() -> Boolean): Has<A, Project> = Has(predicate, context.project, this)

    infix fun <T, F : Feature<T>> has(featureType: KClass<F>): HasFeature<A, T, F> = HasFeature(featureType, this)

    infix fun hasNot(predicate: Project.() -> Boolean): Has<A, Project> = has { !predicate(this) }

}