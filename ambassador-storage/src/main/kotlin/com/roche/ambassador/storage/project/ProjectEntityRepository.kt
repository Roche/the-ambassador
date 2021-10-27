package com.roche.ambassador.storage.project

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface ProjectEntityRepository : PagingAndSortingRepository<ProjectEntity, Long> {

    @Query("delete from project", nativeQuery = true)
    @Modifying
    override fun deleteAll()
    override fun findById(id: Long): Optional<ProjectEntity>
}
