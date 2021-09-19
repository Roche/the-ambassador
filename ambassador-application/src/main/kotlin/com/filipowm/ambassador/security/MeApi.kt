package com.filipowm.ambassador.security

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/me")
internal open class MeApi {

    @GetMapping
    fun me(@AuthenticationPrincipal principal: Principal) = principal

}