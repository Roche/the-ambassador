package com.roche.ambassador.model.source

object PersonalProjectCriteria {

    fun hasAtLeastStars(starsCount: Int): CriterionVerifier = {
        it.stats.stars ?: 0 >= starsCount
    }
}
