package com.roche.ambassador.collections

class HashBiMap<K : Any, V : Any>(capacity: Int = 16, vararg pairs: Pair<K, V>) : AbstractBiMap<K, V>(HashMap(capacity), HashMap(capacity)) {

    init {
        pairs.forEach { this[it.first] = it.second }
    }

    companion object {
        fun <K : Any, V : Any> create(map: Map<K, V>): HashBiMap<K, V> {
            val bimap = HashBiMap<K, V>()
            bimap.putAll(map)
            return bimap
        }
    }
}
