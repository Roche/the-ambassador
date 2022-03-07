package com.roche.ambassador.groups

import com.roche.ambassador.commons.api.Paged
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

@Api("/groups")
@Tag(name = "Groups API", description = "API to read or search indexed groups")
internal class GroupsApi(private val groupsService: GroupsService) {

    @Operation(summary = "Get indexed group by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Single indexed group"),
        ApiResponse(responseCode = "404", description = "Group not found")
    )
    @GetMapping("{id}")
    suspend fun get(@PathVariable @Min(1) id: Long): GroupDto? {
        return groupsService.getGroup(id)
    }

    @Operation(summary = "Search for indexed groups")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "List of indexed groups matching provided query")
    )
    @GetMapping
    suspend fun search(
        query: ListGroupsQuery,
        @PageableDefault(size = 25)
        pageable: Pageable
    ): Paged<SimpleGroupDto> {
        return groupsService.search(query, pageable)
    }
}
