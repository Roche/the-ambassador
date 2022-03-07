package com.roche.ambassador.storage.project

import com.roche.ambassador.storage.Lookup
import org.hibernate.jpa.QueryHints.HINT_CACHEABLE
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*
import java.util.stream.Stream
import javax.persistence.QueryHint

interface ProjectEntityRepository : PagingAndSortingRepository<ProjectEntity, Long> {

    @QueryHints(value = [
        QueryHint(name = HINT_CACHEABLE, value = "false")
    ])
    @Query("SELECT p FROM ProjectEntity p")
    fun streamAllForAnalysis(): Stream<ProjectEntity>

    @Query("DELETE FROM project", nativeQuery = true)
    @Modifying
    override fun deleteAll()

    @EntityGraph(value = "Project.statsHistory")
    override fun findById(id: Long): Optional<ProjectEntity>

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
            WHERE project.last_indexing_id = :indexingId
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
            WHERE length(topics) > 0
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
            GROUP BY languages;
            """,
        nativeQuery = true
    )
    fun findAllLanguages(): List<Lookup>

    @Query(
        value = """
            SELECT *
            FROM project
            WHERE cast(project->'parent'->>'id' AS bigint) = :id
            """,
        nativeQuery = true
    )
    fun findAllByParentId(id: Long): List<ProjectEntity>
}
