package com.roche.ambassador.storage.project

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface ProjectHistoryRepository : PagingAndSortingRepository<ProjectHistoryEntity, Long> {

    @Query("SELECT ph FROM ProjectHistoryEntity ph WHERE ph.parent.id = :id")
    fun findByProjectId(@Param("id") id: Long, pageable: Pageable): Page<ProjectHistoryEntity>
}
