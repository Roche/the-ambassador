package com.roche.ambassador.model

enum class Visibility(val level: Int) {
    PUBLIC(0),
    INTERNAL(1),
    PRIVATE(2),
    UNKNOWN(99)
    ;

    fun getThisAndLessStrict(): List<Visibility> {
        return values().filter { it.level <= this.level }
    }

    fun isMoreStrictThan(visibility: Visibility): Boolean = getThisAndLessStrict().contains(visibility)
}
