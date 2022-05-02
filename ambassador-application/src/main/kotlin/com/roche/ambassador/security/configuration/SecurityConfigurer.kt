package com.roche.ambassador.security.configuration

import org.springframework.security.config.web.server.ServerHttpSecurity

internal interface SecurityConfigurer {

    fun configure(http: ServerHttpSecurity)

}
