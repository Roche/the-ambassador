package com.roche.ambassador.security.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.security.AmbassadorUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableReactiveMethodSecurity
@ConditionalOnProperty(prefix = "ambassador.security", name = ["enabled"], havingValue = "false", matchIfMissing = false)
internal class NoSecurityConfiguration {

    private val log by LoggerDelegate()

    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        log.warn("Disabling web security!")
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
