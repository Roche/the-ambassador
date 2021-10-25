package com.filipowm.ambassador.storage.project

import com.filipowm.ambassador.GenerationSpec
import com.filipowm.ambassador.fake.FakeSource
import com.filipowm.ambassador.model.ScorecardCalculator
import com.filipowm.ambassador.model.feature.FeatureReaders
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.score.ActivityScorePolicy
import com.filipowm.ambassador.model.score.CriticalityScorePolicy
import com.filipowm.ambassador.model.source.ProjectSource
import kotlinx.coroutines.runBlocking

object ProjectCreator {

    private val source: FakeSource = FakeSource(GenerationSpec(1, false))

    @Suppress("UNCHECKED_CAST")
    fun create(id: String = source.createFakeId().toString()): Project {
        return runBlocking {
            val project = source.getById(id).get()

            FeatureReaders
                .all()
                .map { project.readFeature(it, source as ProjectSource<Any>) }

            val scorecard = ScorecardCalculator(
                setOf(
                    ActivityScorePolicy,
                    CriticalityScorePolicy
                )
            ).calculateFor(project)
            project.scorecard = scorecard
            project
        }
    }
}