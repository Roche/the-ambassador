package com.filipowm.ambassador.model.source

interface ForkedProjectCriteria<T> {

    fun includeAll(): CriterionVerifier<T>
    fun excludeAll(): CriterionVerifier<T>

    fun lastActivityAfterParent(): CriterionVerifier<T>
    fun lastActivityNotEarlierThan(): CriterionVerifier<T>
    fun hasStars(stars: Int): CriterionVerifier<T>

}
