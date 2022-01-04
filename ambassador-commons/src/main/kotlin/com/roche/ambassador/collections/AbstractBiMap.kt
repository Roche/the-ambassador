package com.roche.ambassador.collections

abstract class AbstractBiMap<K : Any, V : Any> protected constructor(
    private val direct: MutableMap<K, V>,
    private val reverse: MutableMap<V, K>
) : MutableBiMap<K, V> {
    override val size: Int
        get() = direct.size

    override val inverse: MutableBiMap<V, K> by lazy {
        object : AbstractBiMap<V, K>(reverse, direct) {
            override val inverse: MutableBiMap<K, V>
                get() = this@AbstractBiMap
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> by lazy {
        BiMapSet(direct.entries, { it.key }, { BiMapEntry(it) })
    }
    override val keys: MutableSet<K> by lazy {
        BiMapSet(direct.keys, { it }, { it })
    }
    override val values: MutableSet<V>
        get() = inverse.keys

    constructor() : this(HashMap(), HashMap())

    override fun forcePut(key: K, value: V): V? {
        val oldValue = direct.put(key, value)
        oldValue?.let { reverse.remove(it) }
        val oldKey = reverse.put(value, key)
        oldKey?.let { direct.remove(it) }
        return oldValue
    }

    override fun put(key: K, value: V): V? {
        require(value !in reverse) { "BiMap already contains value $value" }
        return forcePut(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        from.values.forEach { value ->
            require(value !in reverse) { "BiMap already contains value $value" }
        }
        from.entries.forEach { forcePut(it.key, it.value) }
    }

    override fun remove(key: K): V? {
        val oldValue = direct.remove(key)
        oldValue?.let { reverse.remove(it) }
        return oldValue
    }

    override fun clear() {
        direct.clear()
        reverse.clear()
    }

    override operator fun get(key: K): V? {
        return direct[key]
    }

    override fun containsKey(key: K): Boolean {
        return key in direct
    }

    override fun containsValue(value: V): Boolean {
        return value in reverse
    }

    override fun isEmpty(): Boolean {
        return direct.isEmpty()
    }

    private inner class BiMapSet<T : Any>(
        private val elements: MutableSet<T>,
        private val keyGetter: (T) -> K,
        private val elementWrapper: (T) -> T
    ) : MutableSet<T> by elements {
        override fun remove(element: T): Boolean {
            if (element !in this) {
                return false
            }

            val key = keyGetter(element)
            val value = direct.remove(key) ?: return false
            try {
                reverse.remove(value)
            } catch (throwable: Throwable) {
                direct[key] = value
                throw throwable
            }
            return true
        }

        override fun clear() {
            direct.clear()
            reverse.clear()
        }

        override fun iterator(): MutableIterator<T> {
            val iterator = elements.iterator()
            return BiMapSetIterator(iterator, keyGetter, elementWrapper)
        }
    }

    private inner class BiMapSetIterator<T : Any>(
        private val iterator: MutableIterator<T>,
        private val keyGetter: (T) -> K,
        private val elementWrapper: (T) -> T
    ) : MutableIterator<T> {
        private var last: T? = null

        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): T {
            val element = iterator.next().apply {
                last = this
            }
            return elementWrapper(element)
        }

        override fun remove() {
            checkNotNull(last) { "Move to an element before removing it" }
            try {
                val key = keyGetter(last!!)
                val value = direct[key] ?: error("BiMap doesn't contain key $key ")
                reverse.remove(value)
                try {
                    iterator.remove()
                } catch (throwable: Throwable) {
                    reverse[value] = key
                    throw throwable
                }
            } finally {
                last = null
            }
        }
    }

    private inner class BiMapEntry(
        private val entry: MutableMap.MutableEntry<K, V>
    ) : MutableMap.MutableEntry<K, V> by entry {
        override fun setValue(newValue: V): V {
            if (entry.value == newValue) {
                reverse[newValue] = entry.key
                try {
                    return entry.setValue(newValue)
                } catch (throwable: Throwable) {
                    reverse[entry.value] = entry.key
                    throw throwable
                }
            } else {
                check(newValue !in reverse) { "BiMap already contains value $newValue" }
                reverse[newValue] = entry.key
                try {
                    return entry.setValue(newValue)
                } catch (throwable: Throwable) {
                    reverse.remove(newValue)
                    throw throwable
                }
            }
        }
    }
}
