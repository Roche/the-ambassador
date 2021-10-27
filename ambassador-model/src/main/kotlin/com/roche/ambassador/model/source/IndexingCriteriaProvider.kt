package com.roche.ambassador.model.source

interface IndexingCriteriaProvider<T> {

    fun getForkedProjectCriteria(): ForkedProjectCriteria<T>
    fun getInvalidProjectCriteria(): InvalidProjectCriteria<T>
    fun getPersonalProjectCriteria(): PersonalProjectCriteria<T>
}

typealias CriterionVerifier<T> = (T) -> Boolean