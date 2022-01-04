package com.roche.ambassador.fake

import com.roche.ambassador.Identifiable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

internal class FakeExternalDatastore<ID, T : Identifiable<ID>> private constructor(
    maxSize: Int,
    private val idGenerator: IdGenerator<ID>
) {

    companion object {
        fun <T : Identifiable<Long>> withLong(maxSize: Int = 100): FakeExternalDatastore<Long, T> {
            val generator: IdGenerator<Long> = LongIdGenerator()::generate
            return FakeExternalDatastore(maxSize, generator)
        }

        fun <T : Identifiable<UUID>> withUuid(maxSize: Int = 100): FakeExternalDatastore<UUID, T> {
            val generator: IdGenerator<UUID> = UUID::randomUUID
            return FakeExternalDatastore(maxSize, generator)
        }
    }

    private val data = ConcurrentHashMap<ID, T>(maxSize / 2)

    fun create(value: T): T {
        if (value.getId() != null) {
            throw IllegalArgumentException()
        }
        val id = idGenerator.invoke()
        value.setId(id)
        data[id] = value
        return value
    }

    fun read(id: ID): Optional<T> = Optional.ofNullable(data.get(id))

    fun update(value: T): T {
        val id = value.getId() ?: throw IllegalArgumentException()
        if (!data.containsKey(id)) {
            throw IllegalStateException()
        }
        data[id] = value
        return value
    }

    fun delete(id: ID): Boolean {
        if (!data.contains(id)) {
            throw IllegalStateException()
        }
        return data.remove(id) != null
    }

    private class LongIdGenerator {
        private val store = AtomicLong(0)

        fun generate(): Long = store.incrementAndGet()
    }
}

private typealias IdGenerator<ID> = () -> ID
