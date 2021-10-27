package com.roche.ambassador.extensions

fun Enum<*>.toPrettyString(): String {
    return this.toString()
        .replace('_', ' ')
        .capitalizeFully()
}