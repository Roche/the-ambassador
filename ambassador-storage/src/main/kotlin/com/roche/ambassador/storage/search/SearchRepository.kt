package com.roche.ambassador.storage.search

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchRepository<T, Q : SearchQuery> {

    fun search(query: Q, pageable: Pageable): Page<T>

}