package com.filipowm.ambassador.model.source

interface InvalidProjectCriteria<T> {

    fun hasDefaultBranch(): CriterionVerifier<T>
    fun isRepositoryNotEmpty(): CriterionVerifier<T>
    fun canCreateMergeRequest(): CriterionVerifier<T>
    fun canForkProject(): CriterionVerifier<T>
}
