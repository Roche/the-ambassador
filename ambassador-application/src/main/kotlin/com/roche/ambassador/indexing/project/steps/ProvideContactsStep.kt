package com.roche.ambassador.indexing.project.steps

import com.roche.ambassador.indexing.project.IndexingChain
import com.roche.ambassador.indexing.project.IndexingContext
import com.roche.ambassador.model.project.AccessLevel
import com.roche.ambassador.model.project.Contact
import org.springframework.stereotype.Component

@Component
class ProvideContactsStep : IndexingStep {
    override suspend fun handle(context: IndexingContext, chain: IndexingChain) {
        context.source.readMembers(context.project.id.toString())
            .filter { it.accessLevel == AccessLevel.ADMIN }
            .filter { it.email != null || it.webUrl != null }
            .sortedBy { it.name }
            .map { Contact(it.name, it.email, it.webUrl, it.avatarUrl) }
            .forEach(context.project.contacts::add)
    }
}