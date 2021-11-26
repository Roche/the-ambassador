package com.roche.gitlab.api.utils.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.util.*

object EnumSerialization {

    fun <E> toIntSerializer(propertyProvider: (E) -> Optional<Int>): JsonSerializer<E> {
        return PropertySerializer(
            { generator, value -> generator.writeNumber(value) },
            propertyProvider
        )
    }

    fun <E> fromIntDeserializer(enumProvider: (Int) -> Optional<E>): JsonDeserializer<E> {
        return PropertyDeserializer(
            { Optional.ofNullable(it.intValue) },
            enumProvider
        )
    }

    fun <E> toStringSerializer(propertyProvider: (E) -> Optional<String>): JsonSerializer<E> {
        return PropertySerializer(
            { generator, value -> generator.writeNumber(value) },
            propertyProvider
        )
    }

    fun <E> fromStringDeserializer(enumProvider: (String) -> Optional<E>): JsonDeserializer<E> {
        return PropertyDeserializer(
            { Optional.ofNullable(it.valueAsString) },
            enumProvider
        )
    }

    class PropertyDeserializer<T, E>(
        private val valueReader: (JsonParser) -> Optional<T>,
        private val enumProvider: (T) -> Optional<E>
    ) : StdDeserializer<E>(Unit::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): E? {
            return valueReader.invoke(p)
                .flatMap { enumProvider.invoke(it) }
                .orElse(null)
        }
    }

    class PropertySerializer<T, E>(
        private val valueWriter: (JsonGenerator, T) -> Unit,
        private val propertyProvider: (E) -> Optional<T>
    ) : StdSerializer<E>(Unit::class.java, true) {
        override fun serialize(value: E, gen: JsonGenerator, provider: SerializerProvider): Unit = propertyProvider.invoke(value).ifPresent { valueWriter.invoke(gen, it) }
    }
}
