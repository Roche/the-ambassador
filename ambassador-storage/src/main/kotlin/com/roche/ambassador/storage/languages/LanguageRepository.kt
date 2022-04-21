package com.roche.ambassador.storage.languages

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface LanguageRepository : PagingAndSortingRepository<Language, UUID> {

    @Query("DELETE FROM languages_lookup", nativeQuery = true)
    @Modifying
    override fun deleteAll()
}
