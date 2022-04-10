package com.roche.ambassador.lookups

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.commons.api.toPaged
import com.roche.ambassador.storage.Lookup
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

internal sealed class LookupService<T : Lookup, R : PagingAndSortingRepository<T, *>>(private val lookupRepository: R) {

    fun list(pageable: Pageable): Paged<LookupDto> {
        return lookupRepository.findAll(pageable)
            .map { it.toDto() }
            .toPaged()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    open fun refreshLookup() {
        lookupRepository.deleteAll()
        val lookups = retrieveLookups()
        lookupRepository.saveAll(lookups)
    }

    abstract fun retrieveLookups(): List<T>

}