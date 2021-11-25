package com.roche.ambassador.project

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.storage.project.ProjectEntityRepository
import com.roche.ambassador.storage.project.ProjectGroupProjection
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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

    @Operation(summary = "Get project history", description = "See how project changed over time", tags = ["project"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "History of indexed project"),
        ApiResponse(responseCode = "404", description = "Project not found")
    )
    @GetMapping("{id}/history")
    open suspend fun history(
        @PathVariable @Min(1) id: Long,
        @PageableDefault(size = 25, sort = ["indexedDate"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): Paged<ProjectHistoryDto> {
        return projectService.getProjectHistory(id, pageable)
    }

    @Operation(summary = "Search for indexed projects", description = "", tags = ["project"])
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "List of indexed project matching provided query")
    )
    @GetMapping
    open suspend fun search(
        query: ListProjectsQuery,
        @PageableDefault(size = 25)
        pageable: Pageable
    ): Paged<SimpleProjectDto> {
        return projectService.search(query, pageable)
    }
}
