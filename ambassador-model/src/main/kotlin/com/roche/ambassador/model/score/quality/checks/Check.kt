package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.score.quality.PartialCheckResult

interface Check {

    companion object {
        const val MIN_CONFIDENCE = 0
        const val MAX_CONFIDENCE = 10
        const val BRANCH_PROTECTION = "branch-protection"
    }

    fun name(): String
    fun check(features: Features): PartialCheckResult

}
