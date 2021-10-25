package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.Feature
import java.util.*
import kotlin.reflect.KClass

data class Features(private val holder: MutableSet<Feature<*>> = mutableSetOf()) : MutableSet<Feature<*>> by holder {

    constructor(vararg features: Feature<*>) : this() {
        addAll(features)
    }

    fun <T : Feature<*>> find(featureType: Class<T>): Optional<T> {
        val feature = holder.filter { featureType.isInstance(it) }
            .map { featureType.cast(it) }
            .firstOrNull()
        return Optional.ofNullable(feature)
    }

    fun <T : Feature<*>> find(featureType: KClass<T>): Optional<T> = find(featureType.java)

    fun <T : Feature<*>> ifPresent(featureType: KClass<T>, handler: (T) -> Unit) = find(featureType.java).ifPresent(handler)

    fun <T : Feature<*>> ifExists(featureType: KClass<T>, handler: (T) -> Unit) = find(featureType.java).ifPresent {
        it.ifExists(handler as (Feature<out Any?>) -> Unit)
    }

}