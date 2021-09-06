package com.filipowm.ambassador.model.project

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.filipowm.ambassador.model.score.ActivityScorePolicy
import com.filipowm.ambassador.model.score.CriticalityScorePolicy
import com.filipowm.ambassador.model.score.CriticalityScorePolicy.round
import com.filipowm.ambassador.model.stats.Statistics
import com.filipowm.ambassador.model.stats.Timeline
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
    val protectedBranches: List<ProtectedBranch>,
    val stats: Statistics,
    val createdDate: LocalDate,
    val lastUpdatedDate: LocalDate?,
    @JsonIgnore val issues: Issues?,
    @JsonIgnore val commits: Timeline?,
    @JsonIgnore val releases: Timeline?,
    val features: Features,
    val files: Files,
    val languages: Map<String, Float>?,
    val contributors: Contributors
) {

    private var scores: Scores? = null

    //
    @JsonGetter("scores")
    fun getScores(): Scores {
        if (scores == null) {
            val activityScore = ActivityScorePolicy.calculateScoreOf(this)
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
    fun getDaysSinceLastUpdate(): Long {
        return ChronoUnit.DAYS.between(lastUpdatedDate, LocalDate.now())
    }

    @JsonIgnore
    fun getDaysSinceCreation(): Long {
        return ChronoUnit.DAYS.between(createdDate, LocalDate.now())
    }

    @JsonIgnore
    fun calculateCriticalityScore(): Float {
        return 0.0f
    }

    @JsonIgnore
    fun getScorecard(): Float {
        return 0.0f
    }

    @JsonIgnore
    fun getMainLanguage(): String? {
        return languages!!.maxByOrNull { it.value }
            ?.key
    }
}
