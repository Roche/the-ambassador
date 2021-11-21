package com.roche.ambassador.storage.project

import com.roche.ambassador.GenerationSpec
import com.roche.ambassador.fake.FakeSource
import com.roche.ambassador.model.ScorecardCalculator
import com.roche.ambassador.model.feature.FeatureReaders
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.score.ActivityScorePolicy
import com.roche.ambassador.model.score.CriticalityScorePolicy
import com.roche.ambassador.model.source.ProjectSource
import kotlinx.coroutines.runBlocking

object ProjectCreator {

    private val source: FakeSource = FakeSource(GenerationSpec(1, false))

    @Suppress("UNCHECKED_CAST")
    fun create(id: String = source.createFakeId().toString()): Project {
        return runBlocking {
            val project = source.getById(id).get()

            FeatureReaders
                .all()
                .map { project.readFeature(it, source as ProjectSource) }

            val scorecard = ScorecardCalculator.withAllPolicies().calculateFor(project)
            project.scorecard = scorecard
            project
        }
    }
}