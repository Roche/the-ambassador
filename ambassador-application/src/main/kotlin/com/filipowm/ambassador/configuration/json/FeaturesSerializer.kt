package com.filipowm.ambassador.configuration.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.filipowm.ambassador.model.feature.Features

internal object FeaturesSerializer: StdSerializer<Features>(Features::class.java) {
    override fun serialize(value: Features, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        value.forEach {
            val pair = it.makeIndexable()
            if (pair != null) {
                gen.writeFieldName(pair.first)
                when(pair.second) {
                    is String -> gen.writeString(pair.second as String)
                    is Boolean -> gen.writeBoolean(pair.second as Boolean)
                    is Double -> gen.writeNumber(pair.second as Double)
                    is Int -> gen.writeNumber(pair.second as Int)
                    is Float -> gen.writeNumber(pair.second as Float)
                    is Long -> gen.writeNumber(pair.second as Long)
                    is Raw -> gen.writeRawValue((pair.second as Raw).json)
                    else -> gen.writeObject(pair.second)
                }
            }
        }
        gen.writeEndObject()
    }

}