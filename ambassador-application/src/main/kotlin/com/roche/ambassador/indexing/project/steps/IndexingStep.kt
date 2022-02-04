package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import org.springframework.core.Ordered

interface IndexingStep : Ordered {

    suspend fun handle(context: IndexingContext, chain: IndexingChain)
}
