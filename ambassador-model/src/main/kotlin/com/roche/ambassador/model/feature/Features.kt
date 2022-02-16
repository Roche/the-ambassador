package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Feature
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

    fun <T : Feature<*>> findWithValue(featureType: KClass<T>): Optional<T> {
        return find(featureType)
            .filter { it.exists() && it.value().exists() }
    }

    fun <T, U : Feature<T>> findValue(featureType: KClass<U>): Optional<T> {
        return findWithValue(featureType)
            .map { it.value() }
            .map { it.get() }
    }

    fun <T : Feature<*>> find(featureType: KClass<T>): Optional<T> = find(featureType.java)
}
