package com.filipowm.ambassador.model.score

interface TestCalculator<T> {

    fun calculate(data: T): Double

}