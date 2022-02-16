package com.roche.ambassador.model.score.quality

import com.roche.ambassador.model.Explanation

data class CheckResult(
    val checkName: String,
    val checkDescription: String,
    val score: Int,
    val confidence: Int,
    val importance: Importance,
    val attributes: Map<QualityAttribute, Double>,
    val explanation: Explanation,
)