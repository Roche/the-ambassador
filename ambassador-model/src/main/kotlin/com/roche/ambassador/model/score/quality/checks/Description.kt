package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.project.Project
import java.util.*

internal object Description : StringLengthCheck() {

    private const val MIN_LENGTH: Int = 10

    override fun name(): String = Check.DESCRIPTION
    override fun minLength(): Int = MIN_LENGTH
    override fun readStringLength(project: Project): Optional<Int> = Optional.ofNullable(project.description).map { it.length }

    override fun buildExplanation(featureValue: Int, score: Double, builder: Explanation.Builder) {
        builder
            .description("Project description")
            .addDetails("$score for description with $featureValue chars length.")
    }
}
