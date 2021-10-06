package com.filipowm.ambassador.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.filipowm.ambassador.model.project.Project

data class Scorecard(
    val value: Double = 0.0,
    val subScores: Set<Score> = setOf()
) : AbstractScore("Scorecard", setOf(), subScores) {

    @JsonIgnore
    var project: Project? = null

    @JsonIgnore
    fun project() = project

    fun isCalculated() = subScores.isNotEmpty() || value > 0.0

    override fun value(): Double = value

    @JsonIgnore
    override fun features(): Set<Feature<Any>> = setOf()

    override fun subScores(): Set<Score> = subScores

    @JsonProperty("explanation")
    override fun explain(): Explanation {
        return Explanation.no("None")
    }

    companion object {
        fun notCalculated(project: Project): Scorecard = of(project, 0.0, setOf())
        fun of(project: Project, value: Double, scores: Set<Score>): Scorecard {
            val scorecard = Scorecard(value, scores)
            scorecard.project = project
            return scorecard
        }
    }
}