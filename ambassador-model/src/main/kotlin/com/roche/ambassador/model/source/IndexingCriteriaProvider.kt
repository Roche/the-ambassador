package com.roche.ambassador.model.source

import com.roche.ambassador.model.project.Project

object IndexingCriteriaProvider {

    fun getInvalidProjectCriteria(): InvalidProjectCriteria = InvalidProjectCriteria
    fun getPersonalProjectCriteria(): PersonalProjectCriteria = PersonalProjectCriteria
}

typealias CriterionVerifier = Project.() -> Boolean
