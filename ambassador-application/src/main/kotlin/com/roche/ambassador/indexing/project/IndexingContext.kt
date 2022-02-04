package com.roche.ambassador.indexing.project

import com.roche.ambassador.configuration.properties.IndexerProperties
import com.roche.ambassador.indexing.Continuation
import com.roche.ambassador.indexing.project.steps.IndexingStep
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.project.ProjectEntity
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

data class IndexingContext(
    val project: Project,
    val source: ProjectSource,
    val coroutineScope: CoroutineScope,
    var currentStep: KClass<out IndexingStep>? = null,
    var entity: ProjectEntity? = null,
    val config: IndexerProperties,
    val indexing: Indexing,
    val continuation: Continuation
)
