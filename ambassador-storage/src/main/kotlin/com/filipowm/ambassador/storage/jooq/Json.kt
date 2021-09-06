package com.filipowm.ambassador.storage.jooq

import org.jooq.Field
import org.jooq.impl.DSL

class Json(private val field: Field<*>) {

    init {
        require(field.dataType.isJSON) { "Field '${field.name}' is not of JSON or JSONB type" }
    }

    fun <T> field(name: String, type: Class<T>): Field<T> {
        return DSL.field("{0} ->> {1}", type, field, name)
    }
}
