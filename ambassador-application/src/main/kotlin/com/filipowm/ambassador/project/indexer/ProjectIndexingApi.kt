package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.security.HasAdminPermission
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects/indexer")
@HasAdminPermission
internal open class ProjectIndexingApi(private val service: ProjectIndexingService) {

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex(): IndexingDto {
        return service.reindex()
    }

    @GetMapping("{id}")
    suspend fun reindexOne(@PathVariable @Min(1) id: Long): Project? {
        return service.reindex(id)
    }

    @DeleteMapping
    suspend fun forciblyStopAll(@RequestParam("terminate", required = false) terminate: Optional<Boolean>) {
        service.forciblyStopAll(terminate.orElse(false))
    }

    @DeleteMapping("{indexingId}")
    suspend fun forciblyStop(@PathVariable indexingId: UUID, @RequestParam("terminate", required = false) terminate: Optional<Boolean>) {
        service.forciblyStop(indexingId, terminate.orElse(false))
    }
}
