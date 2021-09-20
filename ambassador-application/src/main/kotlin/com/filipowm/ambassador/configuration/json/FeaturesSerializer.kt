package com.filipowm.ambassador.configuration.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.filipowm.ambassador.model.feature.Features

internal object FeaturesSerializer : StdSerializer<Features>(Features::class.java) {
    override fun serialize(value: Features, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        value.forEach {
            it.asIndexEntry().with { key, value ->
                gen.writeFieldName(key)
                when (value) {
                    is String -> gen.writeString(value)
                    is Boolean -> gen.writeBoolean(value)
                    is Double -> gen.writeNumber(value)
                    is Int -> gen.writeNumber(value)
                    is Float -> gen.writeNumber(value)
                    is Long -> gen.writeNumber(value)
                    is Raw -> gen.writeRawValue(value.json)
                    else -> gen.writeObject(value)
                }
            }
        }
        gen.writeEndObject()
    }
}
