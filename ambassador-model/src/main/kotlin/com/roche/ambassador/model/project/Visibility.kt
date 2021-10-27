package com.roche.ambassador.model.project

enum class Visibility(val level: Int) {
    PUBLIC(0),
    INTERNAL(1),
    PRIVATE(2),
    UNKNOWN(99)
    ;

    fun getThisAndLessStrict(): List<Visibility> {
        return values().filter { it.level <= this.level }
    }
}
