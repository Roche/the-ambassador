package pl.filipowm.opensource.ambassador.project.indexer

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.filipowm.opensource.ambassador.model.Project
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects/indexer")
internal open class ProjectIndexingApi(private val indexer: ProjectIndexer) {

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex() {
        indexer.reindex()
    }

    @GetMapping("{id}")
    suspend fun reindexOne(@PathVariable @Min(1) id: Long): Project? {
        return indexer.reindex(id)
    }

}