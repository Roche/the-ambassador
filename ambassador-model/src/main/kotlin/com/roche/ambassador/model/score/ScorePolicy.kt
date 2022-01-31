package com.roche.ambassador.model.score

import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.Features

interface ScorePolicy {

    fun calculateScoreOf(features: Features): Score

    companion object {
        fun activity(): ScorePolicy {
            return ActivityScorePolicy
        }
    }
}
