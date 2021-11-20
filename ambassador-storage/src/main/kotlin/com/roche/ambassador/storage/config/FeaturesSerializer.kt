package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.roche.ambassador.model.feature.Features

internal object FeaturesSerializer : StdSerializer<Features>(Features::class.java) {
    override fun serialize(value: Features, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        value.filter { it.isIndexable() }
            .sortedBy { it.name() }.forEach {
                it.withValue { value ->
                    gen.writeFieldName(it.name())
                    when (value) {
                        is String -> gen.writeString(value)
                        is Boolean -> gen.writeBoolean(value)
                        is Double -> gen.writeNumber(value)
                        is Int -> gen.writeNumber(value)
                        is Float -> gen.writeNumber(value)
                        is Long -> gen.writeNumber(value)
                        else -> gen.writeObject(value)
                    }
                }
            }
        gen.writeEndObject()
    }
}
