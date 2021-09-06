package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.commons.api.Message
import com.filipowm.ambassador.model.project.Project
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects/indexer")
internal open class ProjectIndexingApi(private val service: ProjectIndexingService) {

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex(): Message {
        service.reindex()
        return Message("Indexing has started. It may take a long time until finished.")
    }

    @GetMapping("{id}")
    suspend fun reindexOne(@PathVariable @Min(1) id: Long): Project? {
        return service.reindex(id)
    }

    @DeleteMapping
    @GetMapping("/stop")
    suspend fun forciblyStop(@RequestParam("terminate", required = false) terminate: Optional<Boolean>) {
        service.forciblyStop(terminate.orElse(false))
    }
}
