package com.filipowm.ambassador.model.source

interface PersonalProjectCriteria<T> {

    //    fun includeAll(): CriterionVerifier<T>
//    fun excludeAll(): CriterionVerifier<T>
    fun hasAtLeastStars(starsCount: Int): CriterionVerifier<T>

}