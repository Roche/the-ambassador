package pl.filipowm.innersource.ambassador.project

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.filipowm.innersource.ambassador.model.Project
import reactor.core.publisher.Mono
import javax.validation.constraints.Min

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
}