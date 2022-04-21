package com.roche.ambassador.storage.topics

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TopicRepository : PagingAndSortingRepository<Topic, UUID> {

    @Query("DELETE FROM topics_lookup", nativeQuery = true)
    @Modifying
    override fun deleteAll()
}
