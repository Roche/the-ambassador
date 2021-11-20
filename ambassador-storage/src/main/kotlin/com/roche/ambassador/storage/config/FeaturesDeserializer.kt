package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.feature.FeatureNameLookup
import com.roche.ambassador.model.feature.Features

internal object FeaturesDeserializer : StdDeserializer<Features>(Features::class.java) {

    private val log by LoggerDelegate()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun deserialize(p: JsonParser, ctx: DeserializationContext): Features {
        val features = Features()
        val om = p.codec as ObjectMapper
        val tree: JsonNode = p.codec.readTree(p)
        for ((key, value) in tree.fields()) {
            val featureType = FeatureNameLookup.getFeatureType(key)
            if (featureType.isPresent) {
                val feature = om.treeToValue(value, featureType.get().java)
                features.add(feature)
            } else {
                log.debug("No feature found for key {}", key)
            }
        }
        return features
    }
}
