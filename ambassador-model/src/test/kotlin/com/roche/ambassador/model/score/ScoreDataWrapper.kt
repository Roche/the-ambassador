package com.roche.ambassador.model.score

import com.roche.ambassador.model.dataproviders.ProjectGenerator
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.project.Project

sealed class ScoreDataWrapper {

    fun asProject(): Project {
        val project = ProjectGenerator.generate(features())
        enhanceProject(project)
        return project
    }

    open fun enhanceProject(project: Project) {
        // do nothing
    }

    abstract fun features(): Features
}
