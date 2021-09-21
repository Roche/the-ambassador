package com.filipowm.ambassador.storage.indexing

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface IndexingRepository : CrudRepository<Indexing, UUID> {
    @Query("SELECT i FROM Indexing i WHERE i.lock.id IS NOT NULL AND i.target = :target")
    fun findByLockIsNotNullAndTarget(@Param("target") target: String): Optional<Indexing>

    @Query("SELECT i FROM Indexing i WHERE i.lock.id IS NOT NULL")
    fun findAllLocked(): List<Indexing>

    @Query("SELECT i FROM Indexing i WHERE i.status = com.filipowm.ambassador.storage.indexing.IndexingStatus.IN_PROGRESS")
    fun findAllInProgress(): List<Indexing>

    fun findFirstByTargetOrderByStartedDateDesc(target: String): Optional<Indexing>
}