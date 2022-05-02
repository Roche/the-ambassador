package com.roche.ambassador.security.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableConfigurationProperties(SecurityProperties::class)
internal abstract class BaseSecurityConfiguration(private val configurers: List<SecurityConfigurer>) {

    @Bean
    open fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // @formatter:off
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
        configurers.forEach { it.configure(http) }
        configure(http)
        return http.build()
        // @formatter:on
    }

    abstract fun configure(http: ServerHttpSecurity)
}