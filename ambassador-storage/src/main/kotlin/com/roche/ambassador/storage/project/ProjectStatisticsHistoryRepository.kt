package com.roche.ambassador.storage.project

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface ProjectStatisticsHistoryRepository : CrudRepository<ProjectStatisticsHistory, UUID> {

    @Query("FROM ProjectStatisticsHistory ph WHERE ph.projectId = :id ORDER BY ph.date DESC")
    fun findByProjectId(@Param("id") id: Long): List<ProjectStatisticsHistory>

    @Query("FROM ProjectStatisticsHistory ph WHERE ph.projectId = :id AND ph.date >= :startDate ORDER BY ph.date DESC")
    fun findByProjectIdAndDateGreaterThanEqual(@Param("id") id: Long, @Param("startDate") startDate: LocalDateTime): List<ProjectStatisticsHistory>

    @Query(value = "FROM ProjectStatisticsHistory ph WHERE ph.projectId = :id AND ph.date < :endDate ORDER BY ph.date DESC")
    fun findByProjectIdAndDateLessThan(@Param("id") id: Long, @Param("endDate") endDate: LocalDateTime): List<ProjectStatisticsHistory>

    @Query(value = "FROM ProjectStatisticsHistory ph WHERE ph.projectId = :id AND ph.date BETWEEN :startDate AND :endDate ORDER BY ph.date DESC")
    fun findByProjectIdAndDateBetween(@Param("id") id: Long, @Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): List<ProjectStatisticsHistory>

    @Query("DELETE FROM ProjectStatisticsHistory ph WHERE ph.projectId = :projectId AND ph.date BETWEEN :startDate AND :endDate")
    @Modifying
    fun deleteByProjectIdAndDateBetween(@Param("projectId") projectId: Long, @Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime)
}