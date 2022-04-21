package com.roche.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder("description", "value", "maxValue", "details", "children")
class Explanation(
    val description: String? = null,
    val value: Double? = null,
    val maxValue: Double? = null,
    val details: List<String> = listOf(),
    val children: List<Explanation> = listOf()
) {

    companion object {
        fun builder(): Builder {
            return Builder()
        }

        fun simple(description: String): Explanation {
            return Explanation(description)
        }

        fun empty(): Explanation {
            return Explanation()
        }
    }

    fun mergeWith(other: Explanation, mergedValue: Double? = null, mergedMaxValue: Double? = null): Explanation {
        val builder = builder()
            .addChild(this)
            .addChild(other)
        if (mergedValue != null) {
            builder.value(mergedValue)
        }
        if (mergedMaxValue != null) {
            builder.maxValue(mergedMaxValue)
        }
        return builder.build()
    }

    @JsonIgnore
    fun isPresent(): Boolean = description != null || details.isNotEmpty()

    class Builder {

        private var description: String? = null
        private var value: Double? = null
        private var maxValue: Double? = null
        private val details: MutableList<String> = mutableListOf()
        private val children: MutableList<Explanation> = mutableListOf()

        fun description(description: String): Builder {
            this.description = description
            return this
        }

        fun value(value: Double): Builder {
            this.value = value
            return this
        }

        fun maxValue(maxValue: Double): Builder {
            this.maxValue = maxValue
            return this
        }

        fun addDetails(vararg details: String): Builder {
            this.details += details
            return this
        }

        fun addDetails(details: Collection<String>): Builder {
            this.details += details
            return this
        }

        fun addChild(explanation: Explanation): Builder {
            this.children += explanation
            return this
        }

        fun addChild(action: (Builder) -> (Unit)): Builder {
            val childBuilder = builder()
            action(childBuilder)
            return addChild(childBuilder.build())
        }

        fun build(): Explanation = Explanation(description, value, maxValue, details.toList(), children.toList())
    }
}
