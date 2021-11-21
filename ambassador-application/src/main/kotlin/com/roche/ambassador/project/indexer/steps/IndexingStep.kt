package com.roche.ambassador.project.indexer.steps

interface IndexingStep {

    suspend fun handle(context: IndexingContext)

}