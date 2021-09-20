package com.filipowm.ambassador.configuration.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.filipowm.ambassador.model.feature.AbstractFeature
import com.filipowm.ambassador.model.feature.Features

internal object FeaturesDeserializer : StdDeserializer<Features>(Features::class.java) {
    override fun deserialize(p: JsonParser?, ctx: DeserializationContext): Features? {
        if (p == null) {
            return null
        }
        val features = Features()
        val tree: JsonNode = p.codec.readTree(p)
        for ((key, value) in tree.fields()) {
            val transformedValued = if (value.isFloatingPointNumber) {
                value.doubleValue()
            } else if (value.isTextual) {
                value.textValue()
            } else if (value.isBoolean) {
                value.booleanValue()
            } else if (value.isIntegralNumber) {
                value.intValue()
            } else if (value.isNull || value.isEmpty) {
                null
            } else {
                Raw(value.toString())
            }
            if (transformedValued != null) {
                features.add(FeatureHolder(transformedValued, key))
            }
        }
        return features
    }
}

class Raw(val json: String)

class FeatureHolder<T>(value: T, name: String) : AbstractFeature<T>(value, name)
