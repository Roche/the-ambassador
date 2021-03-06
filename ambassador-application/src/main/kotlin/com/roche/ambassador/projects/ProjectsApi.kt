package com.roche.ambassador.projects

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.model.files.DocumentType
import com.roche.ambassador.model.project.Project
import io.github.filipowm.api.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.validation.constraints.Min

@Api("/projects")
@Tag(name = "Projects API", description = "API to read or search indexed projects")
internal class ProjectsApi(private val projectService: ProjectsService) {

    @Operation(summary = "Get indexed project by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Single indexed project"),
        ApiResponse(responseCode = "404", description = "Project not found")
    )
    @GetMapping("{id}")
    suspend fun get(@PathVariable @Min(1) id: Long): Project? {
        return projectService.getProject(id)
    }

    @Operation(summary = "Get project statistics history", description = "See how project changed over time")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Statistics history of indexed project"),
        ApiResponse(responseCode = "404", description = "Project not found")
    )
    @GetMapping("{id}/stats")
    suspend fun stats(@PathVariable @Min(1) id: Long, query: ProjectStatsHistoryQuery): ProjectStatsHistoryDto {
        return projectService.getProjectStatsHistory(id, query)
    }

    @Operation(summary = "Get readme", description = "Read actual project readme if it exists")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Project readme retrieved"),
        ApiResponse(responseCode = "404", description = "Readme not found")
    )
    @GetMapping("{id}/readme")
    suspend fun readme(@PathVariable @Min(1) id: Long): DocumentDto {
        return projectService.getDocument(id, DocumentType.README)
    }

    @Operation(summary = "Get license", description = "Read actual license if it exists")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Project license retrieved"),
        ApiResponse(responseCode = "404", description = "License not found")
    )
    @GetMapping("{id}/license")
    suspend fun license(@PathVariable @Min(1) id: Long): DocumentDto {
        return projectService.getDocument(id, DocumentType.LICENSE)
    }

    @Operation(summary = "Get contribution guide", description = "Read actual contribution guide if it exists")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Project contribution guide retrieved"),
        ApiResponse(responseCode = "404", description = "Contribution guide not found")
    )
    @GetMapping("{id}/contribution-guide")
    suspend fun contributionGuide(@PathVariable @Min(1) id: Long): DocumentDto {
        return projectService.getDocument(id, DocumentType.CONTRIBUTION_GUIDE)
    }

    @Operation(summary = "Search for indexed projects", description = "")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "List of indexed project matching provided query")
    )
    @GetMapping
    suspend fun search(
        query: ListProjectsQuery,
        @PageableDefault(size = 25)
        pageable: Pageable
    ): Paged<SimpleProjectDto> {
        return projectService.search(query, pageable)
    }
}
