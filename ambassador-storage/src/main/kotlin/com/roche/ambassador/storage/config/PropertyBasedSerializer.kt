package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class PropertyBasedDeserializer<T>(baseClass: Class<T>) : StdDeserializer<T>(baseClass) {
    private val deserializationClasses: MutableMap<String, Class<out T>> = hashMapOf()
    fun register(property: String, deserializationClass: Class<out T>) {
        deserializationClasses[property] = deserializationClass
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val mapper: ObjectMapper = p.codec as ObjectMapper
        val tree: JsonNode = mapper.readTree(p)
        val deserializationClass = findDeserializationClass(tree)
            ?: throw JsonMappingException.from(
                ctxt,
                "No registered unique properties found for polymorphic deserialization"
            )
        return mapper.treeToValue(tree, deserializationClass)
    }

    private fun findDeserializationClass(tree: JsonNode): Class<out T>? {
        val fields: Iterator<Map.Entry<String, JsonNode>> = tree.fields()
        var deserializationClass: Class<out T>? = null
        while (fields.hasNext()) {
            val field: Map.Entry<String, JsonNode> = fields.next()
            val property = field.key
            if (deserializationClasses.containsKey(property)) {
                deserializationClass = deserializationClasses[property]
                break
            }
        }
        return deserializationClass
    }
}
