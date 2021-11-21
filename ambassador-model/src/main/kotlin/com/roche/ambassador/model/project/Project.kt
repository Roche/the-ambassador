package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.FeatureReader
import com.roche.ambassador.model.Scorecard
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.LanguagesFeature
import com.roche.ambassador.model.score.ActivityScorePolicy
import com.roche.ambassador.model.score.CriticalityScorePolicy
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Statistics
import java.time.LocalDate

@JsonPropertyOrder("id", "name", "description", "url", "createdDate", "lastActivityDate")
data class Project(
    val id: Long,
    val url: String?,
    val avatarUrl: String?,
    val name: String,
    val description: String?,
    val tags: List<String>,
    val visibility: Visibility,
    val defaultBranch: String?,
    val archived: Boolean,
    val empty: Boolean,
    val forked: Boolean,
    val stats: Statistics,
    val createdDate: LocalDate,
    val lastActivityDate: LocalDate?,
    val permissions: Permissions?,
    val features: Features = Features(),
    var scorecard: Scorecard? = null,
    @JsonIgnore val potentialReadmePath: String? = null,
    @JsonIgnore val potentialLicensePath: String? = null
) {
    private var scores: Scores? = null

    suspend fun readFeature(featureReader: FeatureReader<*>, source: ProjectSource) {
        featureReader.read(this, source)
            .filter { it.exists() }
            .ifPresent { features.add(it) }
    }

    @JsonGetter("scores")
    fun getScores(): Scores {
        if (scores == null) {
            val activityScore = ActivityScorePolicy.calculateScoreOf(this.features).value()
            val criticalityScore = CriticalityScorePolicy.calculateScoreOf(this.features).value()
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
