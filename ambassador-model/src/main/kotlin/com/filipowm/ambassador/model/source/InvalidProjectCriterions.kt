package com.filipowm.ambassador.model.source

import com.filipowm.ambassador.model.criteria.IndexingCriterion

interface InvalidProjectCriterions<T> {

    fun hasRepositorySetUp(): IndexingCriterion<T>

}