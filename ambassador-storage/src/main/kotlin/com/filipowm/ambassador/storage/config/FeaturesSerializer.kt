package com.filipowm.ambassador.storage.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.filipowm.ambassador.model.Value
import com.filipowm.ambassador.model.feature.Features

internal object FeaturesSerializer : StdSerializer<Features>(Features::class.java) {
    override fun serialize(value: Features, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        value.sortedBy { it.name() }.forEach {
            it.asIndexEntry().with { key, value ->
                val unwrappedValue = unwrap(value)
                gen.writeFieldName(key)
                when (unwrappedValue) {
                    is String -> gen.writeString(unwrappedValue)
                    is Boolean -> gen.writeBoolean(unwrappedValue)
                    is Double -> gen.writeNumber(unwrappedValue)
                    is Int -> gen.writeNumber(unwrappedValue)
                    is Float -> gen.writeNumber(unwrappedValue)
                    is Long -> gen.writeNumber(unwrappedValue)
                    is Raw -> gen.writeRawValue(unwrappedValue.json)
                    else -> gen.writeObject(unwrappedValue)
                }
            }
        }
        gen.writeEndObject()
    }

    private fun unwrap(value: Any) : Any {
        return when(value) {
            is Value<*> -> value.get()!!
            else -> value
        }
    }

}
