package pl.filipowm.opensource.ambassador.project

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.filipowm.opensource.ambassador.commons.api.Paged
import pl.filipowm.opensource.ambassador.model.Project
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects")
internal open class ProjectApi(private val projectService: ProjectService) {

    @GetMapping("{id}")
    open suspend fun get(@PathVariable @Min(1) id: Long): Project? {
        return projectService.getProject(id)
    }

    @GetMapping
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        return projectService.list(query, pageable)
    }
}