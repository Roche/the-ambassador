package com.filipowm.ambassador.fake

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.ForkedProjectCriteria
import java.time.LocalDate

object FakeForkedProjectCriteria : ForkedProjectCriteria<FakeProject> {

    override fun excludeAllWithForks(): CriterionVerifier<FakeProject> = {
        it.whenForked { false }
    }

    override fun lastForkActivityBeforeParentMoreThan(days: Long): CriterionVerifier<FakeProject> = {
        it.whenForked {
            // TODO not supported
            true
        }
    }

    override fun lastForkActivityNotEarlierThan(date: LocalDate): CriterionVerifier<FakeProject> = {
        it.whenForked {
            it.lastUpdatedDate?.isAfter(date) ?: true
        }
    }

    override fun forkHasStars(expectedStars: Int): CriterionVerifier<FakeProject> = {
        it.whenForked {
            it.stats.stars >= expectedStars
        }
    }

    private fun FakeProject.whenForked(block: () -> Boolean): Boolean {
        return if (this.forked) {
            block.invoke()
        } else {
            true
        }
    }
}
