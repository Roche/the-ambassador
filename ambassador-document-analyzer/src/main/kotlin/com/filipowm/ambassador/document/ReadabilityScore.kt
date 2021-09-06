package com.filipowm.ambassador.document

data class ReadabilityScore(val value: Float, val score: ReadabilityScoreRange)

enum class ReadabilityScoreRange(val score: Int) {
    VERY_GOOD(5),
    GOOD(4),
    NORMAL(3),
    BAD(2),
    POOR(1)
}
