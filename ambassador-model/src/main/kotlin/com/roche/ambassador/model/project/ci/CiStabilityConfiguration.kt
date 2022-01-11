package com.roche.ambassador.model.project.ci

data class CiStabilityConfiguration(
    val stableThreshold: Double = 0.9,
    val occasionallyFailingThreshold: Double = 0.75,
    val unstableThreshold: Double = 0.2,
)