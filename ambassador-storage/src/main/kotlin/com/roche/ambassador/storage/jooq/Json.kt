package com.roche.ambassador.storage.jooq

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

class Json(private val field: Field<*>) {

    init {
        require(field.dataType.isJSON) { "Field '${field.name}' is not of JSON or JSONB type" }
    }

    fun <T> field(name: String, type: Class<T>): Field<T> {
        return DSL.field("{0} ->> {1}", type, field, name)
    }

    fun inArray(name: String, vararg values: String): Condition {
        val arr = values.mapIndexed { index, v -> "{${index + 2}}" }.joinToString()

        return DSL.condition("jsonb_exists_any(({0} -> {1})::jsonb, array[$arr])", field, name, *values)
    }

    fun <T> jsonField(name: String, type: Class<T>): Field<T> {
        return DSL.field("{0} ->> {1}", type, field, name)
    }
}
