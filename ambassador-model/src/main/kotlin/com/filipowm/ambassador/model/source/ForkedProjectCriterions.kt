package com.filipowm.ambassador.model.source

import com.filipowm.ambassador.model.criteria.IndexingCriterion

interface ForkedProjectCriterions<T> {

    fun includeAll(): IndexingCriterion<T>
    fun excludeAll(): IndexingCriterion<T>

    fun lastActivityAfterParent(): IndexingCriterion<T>
    fun lastActivityNotEarlierThan(): IndexingCriterion<T>
    fun hasStars(stars: Int): IndexingCriterion<T>

}
