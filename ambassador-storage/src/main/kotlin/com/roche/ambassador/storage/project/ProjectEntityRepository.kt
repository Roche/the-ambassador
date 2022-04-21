package com.roche.ambassador.storage.project

import com.roche.ambassador.storage.Lookup
import org.hibernate.jpa.QueryHints.HINT_CACHEABLE
import org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream
import javax.persistence.QueryHint

interface ProjectEntityRepository : PagingAndSortingRepository<ProjectEntity, Long> {

    @QueryHints(value = [
        QueryHint(name = HINT_CACHEABLE, value = "false"),
        QueryHint(name = HINT_FETCH_SIZE, value = "100"),
    ])
    @Query("SELECT p FROM ProjectEntity p WHERE p.subscribed = true")
    fun streamAllForAnalysis(): Stream<ProjectEntity>

    @Query("DELETE FROM project", nativeQuery = true)
    @Modifying
    override fun deleteAll()

    @Modifying
    fun deleteAllBySourceAndLastIndexedDateIsBefore(source: String, date: LocalDateTime): Int

    override fun findById(id: Long): Optional<ProjectEntity>

    fun countAllBySubscribed(subscribed: Boolean): Long

    @Query(
        value = """
            SELECT cast(project->'parent'->>'id' AS bigint) AS groupId, 
                   cast(project->'parent'->>'type' AS text) AS type, 
                   regexp_replace(cast(json_agg(id) AS text), '[\[\] ]', '', 'g') AS projects,  
                   round(cast(sum(score)/count(*) AS numeric), 2) AS score, 
                   round(cast(sum(criticality_score)/count(*) AS numeric), 4) AS criticality, 
                   round(cast(sum(activity_score)/count(*) AS numeric), 0) AS activity,
                   sum(stars) AS stars,
                   sum(cast(project->'stats'->>'forks' AS bigint)) as forks
            FROM project
            WHERE project.last_indexing_id = :indexingId AND project.subscribed = true
            GROUP BY groupId , type
            ORDER BY groupId
            """,
        nativeQuery = true
    )
    fun getProjectsAggregatedByGroupForIndexing(indexingId: UUID): List<ProjectGroupProjection>

    @Query(
        value = """
            SELECT topics as name, COUNT(*) AS count
            FROM project,
                 jsonb_array_elements_text(project.project -> 'topics') AS topics
            WHERE length(topics) > 0 AND subscribed = true
            GROUP BY topics;
            """,
        nativeQuery = true
    )
    fun findAllTopics(): List<Lookup>

    @Query(
        value = """
            SELECT languages AS name, COUNT(*) AS count
            FROM project,
                 jsonb_object_keys(project -> 'features' -> 'languages') AS languages
            WHERE subscribed = true
            GROUP BY languages;
            """,
        nativeQuery = true
    )
    fun findAllLanguages(): List<Lookup>

    @Query(
        value = """
            SELECT *
            FROM project
            WHERE cast(project->'parent'->>'id' AS bigint) = :id AND subscribed = true
            """,
        nativeQuery = true
    )
    fun findAllByParentId(id: Long): List<ProjectEntity>
}
