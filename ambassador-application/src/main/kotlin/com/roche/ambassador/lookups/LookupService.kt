package com.roche.ambassador.lookups

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.commons.api.toPaged
import com.roche.ambassador.storage.Lookup
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

internal sealed class LookupService<T : Lookup, R : PagingAndSortingRepository<T, *>>(protected val lookupRepository: R) {

    fun list(pageable: Pageable): Paged<LookupDto> {
        return lookupRepository.findAll(pageable)
            .map { it.toDto() }
            .toPaged()
    }

    abstract fun synchronizeLookup()

}