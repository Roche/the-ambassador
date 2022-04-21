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

@Api("languages")
@Tag(name = "Languages API", description = "API to read available programming languages")
internal class LanguagesApi(private val languagesService: LanguagesService) {

    @GetMapping
    @Operation(summary = "Get list of available programming languages")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "List of programming languages"),
    )
    suspend fun list(
        @SortDefault.SortDefaults(
            SortDefault(value = ["count"], direction = Sort.Direction.DESC),
            SortDefault(value = ["name"], direction = Sort.Direction.ASC)
        )
        @PageableDefault(size = 10)
        pageable: Pageable
    ): Paged<LookupDto> {
        return languagesService.list(pageable)
    }

    @GetMapping("/synchronize")
    @NoContent
    @HasAdminPermission
    @Operation(summary = "Synchronize programming languages from indexed projects")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Programming languages synchronized")
    )
    suspend fun sync() {
        languagesService.refreshLookup()
    }
}
