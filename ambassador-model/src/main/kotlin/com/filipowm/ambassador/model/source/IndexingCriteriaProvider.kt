package com.filipowm.ambassador.model.source

interface IndexingCriteriaProvider<T> {

    fun getForkedProjectCriteria(): ForkedProjectCriteria<T>
    fun getInvalidProjectCriteria(): InvalidProjectCriteria<T>
//    fun getStaleProjectCriterions(): StaleProjectCriterions<T>
//    fun getPersonalProjectCriterions(): PersonalProjectCriterions<T>
//    fun getInvalidProjectCriterions(): InvalidProjectCriterions<T>
}

typealias CriterionVerifier<T> = (T) -> Boolean