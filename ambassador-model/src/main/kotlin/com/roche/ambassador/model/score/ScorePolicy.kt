package com.roche.ambassador.model.score

import com.roche.ambassador.model.Score
import com.roche.ambassador.model.project.Project

interface ScorePolicy {

    fun calculateScoreOf(project: Project): Score

    companion object {
        fun activity(): ScorePolicy {
            return ActivityScorePolicy
        }
    }
}
