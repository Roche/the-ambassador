package com.roche.ambassador.collections

interface MutableBiMap<K : Any, V : Any> : BiMap<K, V>, MutableMap<K, V> {
    override val values: MutableSet<V>
    override val inverse: MutableBiMap<V, K>

    fun forcePut(key: K, value: V): V?
}
