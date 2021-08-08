package pl.filipowm.opensource.ambassador.project

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.filipowm.opensource.ambassador.model.Project
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/project")
@Validated
open class ProjectController(private val projectService: ProjectService) {

    @GetMapping("{id}")
    open fun get(query: GetProjectQuery): Mono<Project?> {
        if (query.reindex) {
            return projectService.reindex(query.id)
        }
        return projectService.getProject(query.id)
    }

    @GetMapping
    open fun list(pageable: Pageable): Page<SimpleProjectDto> {
        return projectService.list(pageable)
    }

    @GetMapping("/reindex")
    @ResponseStatus(HttpStatus.ACCEPTED)
    open fun index() {
        projectService.reindex()
    }
}