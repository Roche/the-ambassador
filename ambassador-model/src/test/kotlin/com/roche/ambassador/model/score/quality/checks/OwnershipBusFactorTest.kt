package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.MembersFeature
import com.roche.ambassador.model.project.AccessLevel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("Ownership Bus Factor check")
class OwnershipBusFactorTest {

    @ParameterizedTest
    @CsvSource(
        "1,0,0,0",
        "2,0,0,10",
        "2,1,0,10",
        "3,9,4,9",
        "4,9,4,10",
        "4,10,4,10",
        "4,10,11,9",
        "2,0,2,10",
        "2,0,7,9",
        "2,0,14,7",
        "2,0,23,5",
        "2,0,34,2",
        "2,0,46,2",
        "2,0,47,0"
    )
    fun `should calculate score for ownership bus factor`(owners: Int, readers: Int, writers: Int, expectedScore: Int) {
        // given
        val features = createMembers(owners, readers, writers)

        // when
        val result = OwnershipBusFactor.check(features)

        // then
        assertThat(result.checkName).isEqualTo(Check.OWNERSHIP_BUS_FACTOR)
        assertThat(result.score).isEqualTo(expectedScore)
        assertThat(result.explanation.value).isEqualTo(expectedScore.toDouble())
    }

    companion object {
        fun createMembers(owners: Int = 1, readers: Int = 0, writers: Int = 0): Features {
            val members = mapOf(
                AccessLevel.ADMIN to owners,
                AccessLevel.READ to readers,
                AccessLevel.WRITE to writers
            )
            return Features(MembersFeature(members))
        }
    }
}