package com.filipowm.ambassador.project

import com.filipowm.ambassador.commons.api.Paged
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects")
internal open class ProjectApi(private val projectService: ProjectService) {

    @GetMapping("{id}")
    open suspend fun get(@PathVariable @Min(1) id: Long): com.filipowm.ambassador.model.Project? {
        return projectService.getProject(id)
    }

    @GetMapping
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        return projectService.list(query, pageable)
    }
}
