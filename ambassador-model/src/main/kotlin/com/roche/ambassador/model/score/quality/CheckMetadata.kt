package com.roche.ambassador.model.score.quality

data class CheckMetadata(
    val enabled: Boolean,
    val description: String,
    val importance: Importance,
    val attributes: Map<QualityAttribute, Double>)