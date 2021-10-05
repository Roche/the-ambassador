package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.Score
import com.filipowm.ambassador.model.feature.Features

interface ScorePolicy {

    fun calculateScoreOf(features: Features): Score

    companion object {
        fun activity(): ScorePolicy {
            return ActivityScorePolicy
        }
    }
}
