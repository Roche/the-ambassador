package com.roche.ambassador.model.source

import java.time.LocalDate

interface ForkedProjectCriteria<T> {

    fun includeAllWithForks(): CriterionVerifier<T> = { true }
    fun excludeAllWithForks(): CriterionVerifier<T>

    fun lastForkActivityBeforeParentMoreThan(days: Long): CriterionVerifier<T>
    fun lastForkActivityNotEarlierThan(date: LocalDate): CriterionVerifier<T>
    fun forkHasStars(stars: Int): CriterionVerifier<T>

}
