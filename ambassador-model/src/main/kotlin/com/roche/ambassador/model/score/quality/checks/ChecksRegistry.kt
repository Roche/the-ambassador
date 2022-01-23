package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.score.quality.checks.Check.Companion.BRANCH_PROTECTION
import java.util.*

object ChecksRegistry {

    private val checks: Map<String, Check> = mapOf(
        BRANCH_PROTECTION to BranchProtectionCheck
    )

    operator fun get(name: String): Optional<Check> {
        return Optional.ofNullable(checks[name])
    }

}

