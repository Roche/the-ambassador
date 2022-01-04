package com.roche.ambassador.collections

interface BiMap<K : Any, V : Any> : Map<K, V> {
    override val values: Set<V>
    val inverse: BiMap<V, K>
}
