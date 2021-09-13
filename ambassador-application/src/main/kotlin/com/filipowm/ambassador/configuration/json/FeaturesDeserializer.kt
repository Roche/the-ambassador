package com.filipowm.ambassador.configuration.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.filipowm.ambassador.model.feature.AbstractFeature
import com.filipowm.ambassador.model.feature.Features

internal object FeaturesDeserializer: StdDeserializer<Features>(Features::class.java) {
    override fun deserialize(p: JsonParser?, ctx: DeserializationContext): Features? {
        if (p == null) {
            return null
        }
        val features = Features()
        val tree: JsonNode = p.codec.readTree(p)
        for (field in tree.fields()) {
            val value = if (field.value.isFloatingPointNumber) {
                field.value.doubleValue()
            } else if (field.value.isTextual) {
                field.value.textValue()
            } else if (field.value.isBoolean) {
                field.value.booleanValue()
            } else if (field.value.isIntegralNumber) {
                field.value.intValue()
            } else if (field.value.isNull || field.value.isEmpty) {
                null
            } else {
                Raw(field.value.toString())
            }
            if (value != null) {
                features.add(FeatureHolder(value, field.key))
            }
        }
        return features
    }

}

class Raw(val json: String)

class FeatureHolder<T>(value: T, name: String) : AbstractFeature<T>(value, name)