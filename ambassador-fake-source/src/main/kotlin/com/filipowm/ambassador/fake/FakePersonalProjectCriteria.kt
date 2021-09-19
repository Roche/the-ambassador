package com.filipowm.ambassador.fake

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.PersonalProjectCriteria

object FakePersonalProjectCriteria : PersonalProjectCriteria<FakeProject> {
    override fun hasAtLeastStars(starsCount: Int): CriterionVerifier<FakeProject> = {
        it.stats.stars >= starsCount
    }
}
