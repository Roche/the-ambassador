package com.roche.ambassador.model.source

import com.roche.ambassador.model.project.Visibility
import java.time.LocalDate

interface InvalidProjectCriteria<T> {

    fun hasDefaultBranch(): CriterionVerifier<T>
    fun isRepositoryNotEmpty(): CriterionVerifier<T>
    fun canCreateMergeRequest(): CriterionVerifier<T>
    fun canForkProject(): CriterionVerifier<T>
    fun hasVisibilityAtMost(visibility: Visibility): CriterionVerifier<T>
    fun isNotArchived(): CriterionVerifier<T>
    fun hasActivityAfter(date: LocalDate): CriterionVerifier<T>

}
