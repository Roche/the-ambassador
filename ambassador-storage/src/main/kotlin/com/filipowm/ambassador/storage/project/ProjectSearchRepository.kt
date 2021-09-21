package com.filipowm.ambassador.storage.project

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectSearchRepository {

    fun search(query: SearchQuery, pageable: Pageable): Page<ProjectEntity>
}
