package pl.filipowm.opensource.ambassador.project.indexer

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.filipowm.opensource.ambassador.commons.api.Message
import pl.filipowm.opensource.ambassador.model.Project
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects/indexer")
internal open class ProjectIndexingApi(private val indexer: ProjectIndexer) {

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex(): Message {
        indexer.reindex()
        return Message("Indexing has started. It may take a long time until finished.")
    }

    @GetMapping("{id}")
    suspend fun reindexOne(@PathVariable @Min(1) id: Long): Project? {
        return indexer.reindex(id)
    }

}