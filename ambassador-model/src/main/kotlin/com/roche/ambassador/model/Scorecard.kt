package com.roche.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.roche.ambassador.model.project.Project

@JsonPropertyOrder("value")
data class Scorecard(
    val value: Double = 0.0,
    val subScores: Set<Score> = setOf()
) : AbstractScore("Scorecard", setOf(), subScores, false, Explanation.empty()) {

    @JsonIgnore
    var project: Project? = null

    @JsonIgnore
    fun project() = project

    @JsonIgnore
    fun isCalculated() = subScores.isNotEmpty() || value > 0.0

    override fun value(): Double = value

    @JsonIgnore
    override fun features(): Set<Feature<Any>> = setOf()

    override fun subScores(): Set<Score> = subScores

    companion object {
        fun notCalculated(project: Project): Scorecard = of(project, 0.0, setOf())
        fun of(project: Project, value: Double, scores: Set<Score>): Scorecard {
            val scorecard = Scorecard(value, scores)
            scorecard.project = project
            return scorecard
        }
    }
}
