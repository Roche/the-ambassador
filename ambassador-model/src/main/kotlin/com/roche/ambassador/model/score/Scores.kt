package com.roche.ambassador.model.score

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("total")
data class Scores(
    val activity: Double,
    val criticality: Double,
    val total: Double
)
