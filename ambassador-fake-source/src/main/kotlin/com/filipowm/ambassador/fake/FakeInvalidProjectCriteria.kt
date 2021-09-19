package com.filipowm.ambassador.fake

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.InvalidProjectCriteria

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
}
