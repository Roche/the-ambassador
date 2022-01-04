package com.roche.ambassador.storage.advisor

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AdvisoryMessageRepository : CrudRepository<AdvisoryMessageEntity, UUID> {

    fun findAllByProjectIdAndSourceAndClosedDateNull(projectId: Long, source: String): List<AdvisoryMessageEntity>
}
