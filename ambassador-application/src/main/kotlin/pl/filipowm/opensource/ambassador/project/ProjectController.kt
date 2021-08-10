package pl.filipowm.opensource.ambassador.project

import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.filipowm.opensource.ambassador.commons.api.Paged
import pl.filipowm.opensource.ambassador.model.Project

@RestController
@RequestMapping("/project")
//@Validated
open class ProjectController(private val projectService: ProjectService) {

    @GetMapping("{id}")
    open suspend fun get(query: GetProjectQuery): Project? {
        if (query.reindex) {
            return projectService.reindex(query.id)
        }
        return projectService.getProject(query.id)
    }

    @GetMapping
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        return projectService.list(query, pageable)
    }

    @GetMapping("/reindex")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex() {
        projectService.reindex()
    }

}