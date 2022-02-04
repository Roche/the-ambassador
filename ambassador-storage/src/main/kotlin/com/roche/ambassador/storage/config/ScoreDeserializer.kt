package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.Score
import java.util.*

internal object ScoreDeserializer : StdDeserializer<Score>(Score::class.java) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Score {
        val objectMapper = parser.codec as ObjectMapper
        val tree: JsonNode = objectMapper.readTree(parser)
        val name = tree.tryGet("name").map { it.asText() }.orElse("__invalid__")
        val score = tree.tryGet("value").map { it.doubleValue() }.orElse(0.0)
        val features = tree.readNestedValue<Set<String>>("features", parser).orElseGet { setOf() }
        val scores = tree.readNestedValue<Set<Score>>("subScores", parser).orElseGet { setOf() }
        val experimental = tree.tryGet("experimental").map { it.booleanValue() }.orElse(false)
        val explanation = tree.readNestedValue<Explanation>("explanation", parser).orElse(null)
        return Score.finalWithNames(name, score, setOf(), features, scores, experimental, explanation)
    }
}

internal fun JsonNode.tryGet(field: String): Optional<JsonNode> = Optional.ofNullable(get(field))

internal inline fun <reified T> JsonNode.readNestedValue(field: String, parser: JsonParser): Optional<T> {
    val node = tryGet(field)
    if (node.isEmpty) {
        return Optional.empty()
    }
    val nodeValue = node.get().traverse()
    nodeValue.codec = parser.codec
    val typeRef: TypeReference<T> = object : TypeReference<T>() {}
    return Optional.ofNullable(nodeValue.readValueAs(typeRef))
}
