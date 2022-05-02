package com.roche.ambassador.security.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.security.AmbassadorUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity

@Configuration
@EnableReactiveMethodSecurity
@ConditionalOnProperty(prefix = "ambassador.security", name = ["enabled"], havingValue = "false", matchIfMissing = false)
internal class SecurityDisabledConfiguration(configurers: List<SecurityConfigurer>) : BaseSecurityConfiguration(configurers) {

    companion object {
        private val log by LoggerDelegate()
        private val anonymousUser = AmbassadorUser(
            "__local__",
            "__local__",
            "local@localhost",
            mapOf(),
            listOf(AmbassadorUser.ADMIN, AmbassadorUser.USER)
        )
    }

    override fun configure(http: ServerHttpSecurity) {
        log.warn("Disabling web security!")
        //@formatter:off
        http.authorizeExchange()
            .anyExchange().permitAll().and()
            .anonymous()
                .principal(anonymousUser)
                .authorities(anonymousUser.authorities)
        //@formatter:on
    }
}
