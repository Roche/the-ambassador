package com.roche.ambassador.model.score

interface TestCalculator<T> {

    fun calculate(data: T): Double

}