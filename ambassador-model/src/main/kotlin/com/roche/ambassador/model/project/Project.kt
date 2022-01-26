package com.roche.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.FeatureReader
import com.roche.ambassador.model.Scorecard
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.LanguagesFeature
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.score.Scores
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.model.stats.Statistics
import java.time.LocalDate

@JsonPropertyOrder("id", "name", "fullName", "description", "url", "createdDate", "lastActivityDate")
data class Project(
    val id: Long,
    val url: String?,
    val avatarUrl: String?,
    val name: String,
    val fullName: String,
    val description: String?,
    val topics: List<String>,
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
    val parent: Group? = null,
    @JsonIgnore val potentialReadmePath: String? = null,
    @JsonIgnore val potentialLicensePath: String? = null
) {

    suspend fun readFeature(featureReader: FeatureReader<*>, source: ProjectSource) {
        featureReader.read(this, source)
            .filter { it.exists() }
            .ifPresent { features.add(it) }
    }

    @JsonIgnore
    fun getScores(): Scores {
        val immutableScorecard = scorecard
        if (immutableScorecard != null) {
            val total = immutableScorecard.value
            val criticality = immutableScorecard.getSubScoreValueByNameOrZero("criticality")
            val activity = immutableScorecard.getSubScoreValueByNameOrZero("activity")
            return Scores(activity, criticality, total)
        }
        return Scores(0.0, 0.0, 0.0)
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
