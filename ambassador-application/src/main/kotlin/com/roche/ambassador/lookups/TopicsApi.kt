package com.roche.ambassador.lookups

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.security.HasAdminPermission
import io.github.filipowm.api.annotations.Api
import io.github.filipowm.api.annotations.status.NoContent
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.web.bind.annotation.GetMapping

@Api("topics")
@Tag(name = "Topics API", description = "API to read available topics")
internal class TopicsApi(private val topicsService: TopicsService) {

    @GetMapping
    @Operation(summary = "Get list of available topics")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "List of topics"),
    )
    suspend fun list(
        @SortDefault.SortDefaults(
            SortDefault(value = ["count"], direction = Sort.Direction.DESC),
            SortDefault(value = ["name"], direction = Sort.Direction.ASC)
        )
        @PageableDefault(size = 10)
        pageable: Pageable
    ): Paged<LookupDto> {
        return topicsService.list(pageable)
    }

    @GetMapping("/synchronize")
    @NoContent
    @HasAdminPermission
    @Operation(summary = "Synchronize topics from indexed projects")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Topics synchronized")
    )
    suspend fun sync() {
        topicsService.refreshLookup()
    }
}
