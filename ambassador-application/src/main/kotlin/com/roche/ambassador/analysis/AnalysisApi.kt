package com.roche.ambassador.analysis

import com.roche.ambassador.security.HasAdminPermission
import io.github.filipowm.api.annotations.Api
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.ResponseStatus

@Api("/analysis")
@Tag(name = "Analysis API", description = "API handling projects analysis")
@HasAdminPermission
internal class AnalysisApi(private val service: OnDemandAnalysisService) {

    @Operation(summary = "Trigger analysis of all projects within source")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Analysis started"),
        ApiResponse(responseCode = "403", description = "Insufficient privileges, admin needed")
    )
    @RequestMapping(method = [GET, POST])
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun reanalyze() {
        service.analyzeAll()
    }
}