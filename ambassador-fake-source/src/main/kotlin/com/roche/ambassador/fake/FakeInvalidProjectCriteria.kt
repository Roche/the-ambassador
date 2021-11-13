package com.roche.ambassador.fake

import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.model.source.CriterionVerifier
import com.roche.ambassador.model.source.InvalidProjectCriteria
import java.time.LocalDate

object FakeInvalidProjectCriteria : InvalidProjectCriteria<FakeProject> {
    override fun hasDefaultBranch(): CriterionVerifier<FakeProject> = {
        it.defaultBranch != null
    }

    override fun isRepositoryNotEmpty(): CriterionVerifier<FakeProject> = { !it.emptyRepository }

    override fun canCreateMergeRequest(): CriterionVerifier<FakeProject> = {
        // TODO not yet supported
        true
    }

    override fun canForkProject(): CriterionVerifier<FakeProject> = {
        // TODO not yet supported
        true
    }

    override fun hasVisibilityAtMost(visibility: Visibility): CriterionVerifier<FakeProject> = {
        true
    }

    override fun isNotArchived(): CriterionVerifier<FakeProject> = {
        true
    }

    override fun hasActivityAfter(date: LocalDate): CriterionVerifier<FakeProject> = {
        true
    }
}
