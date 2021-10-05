package com.filipowm.ambassador.security

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/me")
@Tag(name = "Me API", description = "Current user API")
internal open class MeApi {

    @Operation(summary = "Get current user details")
    @GetMapping
    suspend fun me(@AuthenticationPrincipal principal: AmbassadorUser) = principal
}
