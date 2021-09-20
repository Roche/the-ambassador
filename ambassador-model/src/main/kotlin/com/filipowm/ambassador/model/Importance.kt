package com.filipowm.ambassador.model

import com.filipowm.ambassador.model.utils.Range

class Importance private constructor(private val value: Double) {

    fun value(): Double = value
    fun name(): Name = Name.forValue(value())

    companion object {
        private val RANGE = Range.bound(0.0, 1.0)
        fun none(): Importance = Importance(0.0)
        fun low(): Importance = Importance(.1)
        fun medium(): Importance = Importance(.5)
        fun high(): Importance = Importance(.8)
        fun custom(value: Double): Importance {
            val adjusted = RANGE.adjust(value)
            return Importance(adjusted)
        }
    }

    override fun toString(): String = "${name()}($value)"
    enum class Name(val minimumValue: Double) {
        LOW(0.0),
        MEDIUM(0.4),
        HIGH(0.8)
        ;

        companion object {
            fun forValue(value: Double): Name {
                var matched = LOW
                for (v in values()) {
                    if (v.minimumValue >= value) {
                        matched = v
                    }
                }
                return matched
            }
        }
    }
}