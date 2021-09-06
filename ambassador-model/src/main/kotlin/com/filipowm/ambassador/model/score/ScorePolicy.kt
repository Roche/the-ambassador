package com.filipowm.ambassador.model.score

interface ScorePolicy<T> {

    fun calculateScoreOf(project: com.filipowm.ambassador.model.Project): T

    companion object {
        fun activity(): ScorePolicy<Double> {
            return ActivityScorePolicy
        }

        fun criticality(): ScorePolicy<Double> {
            return CriticalityScorePolicy
        }

        fun securityHealth(): ScorePolicy<Double> {
            TODO("")
        }

        fun quality(): ScorePolicy<Double> {
            TODO("")
        }
    }
}
