package com.roche.ambassador.model.project.ci

data class CiStability(
    val value: Double,
    val valuePercentage: String,
    val failed: Int,
    val successful: Int,
    val total: Int,
    val state: State
) {
    enum class State {
        STABLE,
        OCCASIONALLY_FAILING,
        UNSTABLE,
        CRITICALLY_UNSTABLE
    }
}