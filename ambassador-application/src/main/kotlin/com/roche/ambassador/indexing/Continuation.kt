package com.roche.ambassador.indexing

import com.roche.ambassador.configuration.properties.IndexingCriteriaProperties
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingStatus
import org.springframework.core.style.ToStringCreator
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Continuation(history: List<Indexing>, props: IndexingCriteriaProperties) {

    val lastActivityAfter: LocalDateTime?
    val unfinishedIndexingIds: List<UUID>
    val resumed: Boolean
    val full: Boolean
    val incrementalOnly: Boolean

    init {
        val sortedHistory = history
            .filter { it.status == IndexingStatus.FINISHED || it.finishedDate?.toLocalDate()?.isEqual(LocalDate.now()) ?: false }
            .sortedByDescending { it.finishedDate }

        val last = if (sortedHistory.isNotEmpty()) {
            sortedHistory.last() // assume last should be finished
        } else {
            null
        }
        this.lastActivityAfter = if (last != null && last.isSuccessful()) {
            last.finishedDate ?: last.startedDate
        } else {
            props.projects.lastActivityAfter
        }
        this.unfinishedIndexingIds = sortedHistory.filterNot { it.isSuccessful() }.map { it.getId()!! }
        this.full = lastActivityAfter == props.projects.lastActivityAfter && unfinishedIndexingIds.isEmpty()
        this.resumed = !full && (unfinishedIndexingIds.isNotEmpty())
        this.incrementalOnly = !resumed && lastActivityAfter != null
    }

    companion object {
        fun none(): Continuation {
            return Continuation(listOf(), IndexingCriteriaProperties())
        }
    }

    override fun toString(): String {
        return ToStringCreator(this)
            .append("lastActivityAfter", lastActivityAfter)
            .append("unfinishedIndexingIds", unfinishedIndexingIds)
            .toString()
    }
}
