package com.roche.ambassador.extensions

import kotlin.math.roundToInt

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}

fun Double.asPercentage(decimals: Int = 0): Double {
    val x100 = this * 100
    return x100.round(decimals)
}

fun Double.asPercentageString(decimals: Int = 0): String = "${asPercentage(decimals)}%"
