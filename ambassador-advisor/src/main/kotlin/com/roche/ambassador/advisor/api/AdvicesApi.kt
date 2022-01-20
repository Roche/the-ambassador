package com.roche.ambassador.advisor.api

import io.github.filipowm.api.annotations.Api
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.validation.constraints.Min

@Api("/projects/{projectId}/advices")
@Tag(name = "Projects API", description = "API to read or search indexed projects")
class AdvicesApi(private val advisorService: AdvisorService) {

    @GetMapping
    suspend fun readAdvices(@PathVariable @Min(1) projectId: Long): List<AdviceDto> = advisorService.readAdvices(projectId)

}