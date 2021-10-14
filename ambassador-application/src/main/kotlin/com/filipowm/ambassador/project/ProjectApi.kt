package com.filipowm.ambassador.project

import com.filipowm.ambassador.commons.api.Paged
import com.filipowm.ambassador.model.project.Project
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Min

@RestController
@RequestMapping("/projects")
@Tag(name = "Project API", description = "API to read or search indexed projects")
internal open class ProjectApi(private val projectService: ProjectService) {

    @Operation(summary = "Get indexed project by ID", tags = ["project"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Single indexed project"),
        ApiResponse(responseCode = "404", description = "Project not found")
    )
    @GetMapping("{id}")
    open suspend fun get(@PathVariable @Min(1) id: Long): Project? {
        return projectService.getProject(id)
    }

    fun xd(): ResponseEntity<String> {
        return ResponseEntity.ok("xd")
    }

    @Operation(summary = "Search for indexed projects", description = "", tags = ["project"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Single indexed project")
    )
    @GetMapping
    open suspend fun list(query: ListProjectsQuery, pageable: Pageable): Paged<SimpleProjectDto> {
        return projectService.list(query, pageable)
    }
}
