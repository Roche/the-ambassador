package com.roche.ambassador.extensions

import java.util.*

fun <T> Iterable<T>.firstAsOptional(predicate: (T) -> Boolean): Optional<T> {
    val result = firstOrNull(predicate)
    return Optional.ofNullable(result)
}