package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.project.Project

interface ScorePolicy<T> {

    fun calculateScoreOf(project: Project): T

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
