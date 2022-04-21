package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext

interface IndexingStep {

    suspend fun handle(context: IndexingContext, chain: IndexingChain)
}
