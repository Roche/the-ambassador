package com.roche.ambassador.indexing

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.security.HasAdminPermission
import io.github.filipowm.api.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.constraints.Min

@Api("/indexing")
@Tag(name = "Indexing API", description = "API handle indexing")
@HasAdminPermission
internal class IndexingApi(private val service: IndexingService) {

    @Operation(summary = "Trigger indexing of all projects within source")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Indexing started"),
        ApiResponse(responseCode = "403", description = "Insufficient privileges, admin needed"),
        ApiResponse(responseCode = "409", description = "Indexing is already in progress")
    )
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reindex(): IndexingDto {
        return service.reindex()
    }

    @Operation(summary = "Trigger indexing of project by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Indexed project"),
        ApiResponse(responseCode = "403", description = "Insufficient privileges, admin needed"),
        ApiResponse(responseCode = "404", description = "Project not found in source")
    )
    @RequestMapping(value = ["/project/{id}"], method = [RequestMethod.GET, RequestMethod.POST])
    suspend fun reindexOne(@PathVariable @Min(1) id: Long): Project? {
        return service.reindex(id)
    }

    @Operation(summary = "Stop all running indexing")
    @ApiResponses(
        ApiResponse(responseCode = "403", description = "Insufficient privileges, admin needed"),
    )
    @DeleteMapping
    suspend fun forciblyStopAll(
        @RequestParam("terminate", required = false)
        @Parameter(description = "Flag if currently indexed projects should stop immediately, allow finish if false, but don't pick new projects from source")
        terminate: Optional<Boolean>
    ) {
        service.forciblyStopAll(terminate.orElse(false))
    }

    @Operation(summary = "Stop running indexing by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Indexing stopped"),
        ApiResponse(responseCode = "403", description = "Insufficient privileges, admin needed"),
        ApiResponse(responseCode = "404", description = "Indexing not found or is not in progress")
    )
    @DeleteMapping("{indexingId}")
    suspend fun forciblyStop(
        @PathVariable indexingId: UUID,
        @Parameter(description = "Flag if currently indexed projects should stop immediately, allow finish if false, but don't pick new projects from source")
        @RequestParam("terminate", required = false) terminate: Optional<Boolean>
    ) {
        service.forciblyStop(indexingId, terminate.orElse(false))
    }
}
