package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.roche.ambassador.model.Score
import java.util.*

internal object ScoreDeserializer : StdDeserializer<Score>(Score::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Score? {
        if (p == null) {
            return null
        }
        val tree: JsonNode = p.codec.readTree(p)
        val name = tree.tryGet("name").map { it.asText() }.orElse("__invalid__")
        val score = tree.tryGet("value").map { it.doubleValue() }.orElse(0.0)
        val features = tree.readNestedValue<Set<String>>("features", p).orElseGet { setOf() }
        val scores = tree.readNestedValue<Set<Score>>("subScores", p).orElseGet { setOf() }
        return Score.finalWithNames(name, score, setOf(), features, scores)
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