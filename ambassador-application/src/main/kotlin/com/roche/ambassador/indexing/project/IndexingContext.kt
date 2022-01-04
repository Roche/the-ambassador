package com.roche.ambassador.indexing.project

import com.roche.ambassador.indexing.project.steps.IndexingStep
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.project.ProjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

data class IndexingContext(
    val project: Project,
    val source: ProjectSource,
    val coroutineScope: CoroutineScope,
    val currentStep: KClass<out IndexingStep>,
    var entity: ProjectEntity? = null,
)
