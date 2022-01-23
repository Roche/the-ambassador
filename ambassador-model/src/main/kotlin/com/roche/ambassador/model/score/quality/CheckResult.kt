package com.roche.ambassador.model.score.quality

data class CheckResult(
    val checkName: String,
    val checkDescription: String,
    val score: Double,
    val confidence: Int,
    val reason: String,
    val importance: Importance,
    val attributes: Map<QualityAttribute, Double>
)