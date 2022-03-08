package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Feature
import java.util.*
import kotlin.reflect.KClass

data class Features(private val holder: MutableMap<Class<out Feature<*>>, Feature<*>> = mutableMapOf()) {

    constructor(vararg features: Feature<*>) : this() {
        addAll(features)
    }

    fun isEmpty(): Boolean = holder.isEmpty()

    fun isNotEmpty(): Boolean = holder.isNotEmpty()

    fun filter(predicate: (Feature<*>) -> Boolean): Collection<Feature<*>> {
        return holder.filterValues(predicate).values
    }

    fun add(feature: Feature<*>) {
        val type = feature::class.java
        if (!holder.containsKey(type)) {
            holder[type] = feature
        }
    }

    fun addAll(features: Array<out Feature<*>>) {
        features.forEach { add(it) }
    }

    fun addAll(features: Iterable<Feature<*>>) {
        features.forEach { add(it) }
    }

    fun addAll(features: Features) {
        features.forEach { add(it) }
    }

    fun forEach(consumer: (Feature<*>) -> Unit) {
        holder.values.forEach(consumer)
    }

    fun <T : Feature<*>> find(featureType: Class<T>): Optional<T> {
        val feature = holder.getOrDefault(featureType, null)
        return Optional.ofNullable(feature).map { featureType.cast(it) }
    }

    fun <T : Feature<*>> findWithValue(featureType: KClass<T>): Optional<T> {
        return find(featureType).filter { it.exists() && it.value().exists() }
    }

    fun <T, U : Feature<T>> findValue(featureType: KClass<U>): Optional<T> {
        return findWithValue(featureType).map { it.value() }.map { it.get() }
    }

    fun <T : Feature<*>> find(featureType: KClass<T>): Optional<T> = find(featureType.java)
}
