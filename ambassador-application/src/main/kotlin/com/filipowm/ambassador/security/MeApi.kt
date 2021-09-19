package com.filipowm.ambassador.security

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/me")
internal open class MeApi {

    @GetMapping
    suspend fun me(@AuthenticationPrincipal principal: OAuth2User) = principal

}