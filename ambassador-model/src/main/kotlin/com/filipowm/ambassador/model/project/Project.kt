package com.filipowm.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.filipowm.ambassador.model.FeatureReader
import com.filipowm.ambassador.model.feature.Features
import com.filipowm.ambassador.model.feature.LanguagesFeature
import com.filipowm.ambassador.model.score.ActivityScorePolicy
import com.filipowm.ambassador.model.score.CriticalityScorePolicy
import com.filipowm.ambassador.model.score.CriticalityScorePolicy.round
import com.filipowm.ambassador.model.source.ProjectSource
import com.filipowm.ambassador.model.stats.Statistics
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Project(
    val id: Long,
    val url: String?,
    val avatarUrl: String?,
    val name: String,
    val description: String?,
    val tags: List<String>,
    val visibility: Visibility,
    val defaultBranch: String?,
    val stats: Statistics,
    val createdDate: LocalDate,
    val lastUpdatedDate: LocalDate?,
    val features: Features = Features(),
    @JsonIgnore val potentialReadmePath: String? = null,
    @JsonIgnore val potentialLicensePath: String? = null
) {

    private var scores: Scores? = null

    suspend fun readFeature(featureReader: FeatureReader<*>, source: ProjectSource<Any>) {
        featureReader.read(this, source)
            .filter { it.exists() }
            .ifPresent { features.add(it) }
    }

    //
    @JsonGetter("scores")
    fun getScores(): Scores {
        if (scores == null) {
            val activityScore = ActivityScorePolicy.calculateScoreOf(this.features).value()
            val criticalityScore = CriticalityScorePolicy.calculateScoreOf(this)
            this.scores = Scores(
                activity = activityScore,
                criticality = criticalityScore,
                total = (criticalityScore * activityScore).round(2)
            )
        }
        return scores as Scores
    }

    @JsonIgnore
    fun getMainLanguage(): String? {
        return features.find(LanguagesFeature::class)
            .map { it.value() }
            .filter { it.exists() }
            .map { it.get() }
            .map { data -> data.maxByOrNull { it.value } }
            .map { it!!.key }
            .orElse(null)
    }
}
