package com.roche.ambassador.model.source

object PersonalProjectCriteria {

    fun hasAtLeastStars(starsCount: Int): CriterionVerifier = { (stats.stars ?: 0) >= starsCount }
}
