package com.roche.ambassador.model

interface Weighted {

    fun weight(): Double
    fun adjustValueByWeight(value: Number): Double = value.toDouble() * weight()

}