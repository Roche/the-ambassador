package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingContext

interface IndexingStep {

    suspend fun handle(context: IndexingContext)

}