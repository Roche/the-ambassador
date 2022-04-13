package com.roche.ambassador.lookups

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.commons.api.toPaged
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.storage.Lookup
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

internal sealed class LookupService<T : Lookup, R : PagingAndSortingRepository<T, *>>(private val lookupRepository: R) {

    companion object {
        private val log by LoggerDelegate()
    }

    @Transactional(readOnly = true)
    open fun list(pageable: Pageable): Paged<LookupDto> {
        return lookupRepository.findAll(pageable)
            .map { it.toDto() }
            .toPaged()
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    open fun refreshLookup() {
        log.info("Triggered refresh of lookups using {}", this::class.simpleName)
        lookupRepository.deleteAll()
        val lookups = retrieveLookups()
        lookupRepository.saveAll(lookups)
        log.info("Lookups refresh using {} finished", this::class.simpleName)
    }

    abstract fun retrieveLookups(): List<T>

}