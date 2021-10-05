package com.filipowm.ambassador.security.configuration

import com.filipowm.ambassador.extensions.LoggerDelegate
import com.filipowm.ambassador.security.AmbassadorUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableReactiveMethodSecurity
@Profile("local")
internal class LocalSecurityConfiguration {

    private val log by LoggerDelegate()

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        log.warn("Disabling web security for local development!")
        // @formatter:off
        return http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable()
            .cors().disable()
            .authorizeExchange()
                .anyExchange().permitAll().and()
            .anonymous()
                .principal(anonymousUser)
                .authorities(anonymousUser.authorities)
                .and()
            .build()
        // @formatter:on
    }

    companion object {
        private val anonymousUser = AmbassadorUser(
            "__local__",
            "__local__",
            "local@localhost",
            mapOf(),
            listOf(AmbassadorUser.ADMIN, AmbassadorUser.USER)
        )
    }
}
