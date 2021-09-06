package com.filipowm.ambassador.model.source

interface IndexingCriteriaProvider<T> {

    fun getForkedProjectCriteria(): ForkedProjectCriteria<T>
    fun getInvalidProjectCriteria(): InvalidProjectCriteria<T>
//    fun getPersonalProjectCriterions(): PersonalProjectCriterions<T>
}

typealias CriterionVerifier<T> = (T) -> Boolean