package com.roche.ambassador.storage.indexing

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface IndexingRepository : CrudRepository<Indexing, UUID> {
    @Query("SELECT i FROM Indexing i WHERE i.lock.id IS NOT NULL AND i.target = :target")
    fun findByLockIsNotNullAndTarget(@Param("target") target: String): Optional<Indexing>

    @Query("SELECT i FROM Indexing i WHERE i.lock.id IS NOT NULL")
    fun findAllLocked(): List<Indexing>

    @Query("SELECT i FROM Indexing i WHERE i.status = com.roche.ambassador.storage.indexing.IndexingStatus.IN_PROGRESS")
    fun findAllInProgress(): List<Indexing>

    fun findFirstByTargetAndStatusOrderByStartedDateDesc(target: String, status: IndexingStatus): Optional<Indexing>

    override fun findAll(): List<Indexing>

    @Query(
        """
              SELECT indexing.*
              FROM indexing
                      LEFT JOIN (SELECT *
                                 FROM indexing
                                 WHERE status = 'FINISHED'
                                 AND source = :source
                                 ORDER BY finished_date DESC
                                 LIMIT 1) finished ON TRUE
              WHERE NOT EXISTS(SELECT * FROM indexing WHERE status = 'FINISHED' AND source = :source)
                OR (
                    ((indexing.finished_date > finished.finished_date AND indexing.finished_date >= now() - INTERVAL '1 DAY') OR indexing.id = finished.id)
                    AND indexing.source = finished.source 
                )
    """, nativeQuery = true)
    fun findLastFinishedAndAllFollowingWithinLastDayForSource(source: String): List<Indexing>
}
