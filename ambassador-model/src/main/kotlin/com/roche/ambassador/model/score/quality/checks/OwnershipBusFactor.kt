package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.MembersFeature
import com.roche.ambassador.model.project.AccessLevel
import com.roche.ambassador.model.score.quality.PartialCheckResult
import java.lang.Math.pow
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/*
Minimum 2 owners regardless of number of members, floor(sqrt(total_members)) otherwise
Score: (expected - owners)^1.5 , (min 0, max 10)
 */
internal object OwnershipBusFactor : Check {

    private const val MINIMUM_OWNERS = 2.0

    override fun name(): String = Check.OWNERSHIP_BUS_FACTOR

    override fun check(features: Features): PartialCheckResult {
        val members = features.findValue(MembersFeature::class).orElseGet { mapOf() }
        val owners = members.owners()
        val other = members.writers() + members.readers()
        val expected = calculateExpectedOwnersCount(owners, other)
        val score = if (owners <= 1) {
            Check.MIN_SCORE
        } else {
            val diff = min(pow(max(expected - owners, 0.0), 1.5).round(0), Check.MAX_SCORE)
            Check.MAX_SCORE - diff
        }
        return PartialCheckResult
            .builder(name())
            .score(score)
            .explanation {
                it.description("Ownership Bus Factor")
                    .value(score)
                    .addDetails("$score for $owners project owners out of expected minimum of ${expected.toInt()}")
                    .addDetails("if $owners are hit by bus, the project likely will become unmaintained")
            }
            .build()
    }

    private fun calculateExpectedOwnersCount(owners: Int, otherMembers: Int): Double {
        return max(MINIMUM_OWNERS, floor(sqrt(owners.toDouble() + otherMembers)))
    }

    private fun Members.owners(): Int = getForAccessLevel(AccessLevel.ADMIN)
    private fun Members.writers(): Int = getForAccessLevel(AccessLevel.WRITE)
    private fun Members.readers(): Int = getForAccessLevel(AccessLevel.READ)

    private fun Members.getForAccessLevel(accessLevel: AccessLevel): Int = getOrDefault(accessLevel, 0)
}

internal typealias Members = Map<AccessLevel, Int>
