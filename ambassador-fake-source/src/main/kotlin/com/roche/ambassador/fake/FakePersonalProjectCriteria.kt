package com.roche.ambassador.fake

import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.PersonalProjectCriteria

object FakePersonalProjectCriteria : PersonalProjectCriteria<FakeProject> {
    override fun hasAtLeastStars(starsCount: Int): CriterionVerifier<FakeProject> = {
        it.stats.stars >= starsCount
    }
}
