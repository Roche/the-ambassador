package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.storage.indexing.Indexing
import java.time.LocalDateTime
import java.util.*

data class IndexingDto(
    val id: UUID,
    val startedBy: String,
    val startedDate: LocalDateTime,
    val lockId: UUID?
) {
    companion object {
        fun from(indexing: Indexing): IndexingDto {
            return IndexingDto(indexing.getId()!!, indexing.startedBy!!, indexing.startedDate, indexing.lock?.getId())
        }
    }
}