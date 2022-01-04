package com.roche.ambassador.advisor.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AdvisorProperties::class)
class AdvisorConfiguration(private val properties: AdvisorProperties) : InitializingBean {

    companion object {
        private val log by LoggerDelegate()
    }

    override fun afterPropertiesSet() {
        log.info("Advisor mode: {}", properties.mode)
    }
}
