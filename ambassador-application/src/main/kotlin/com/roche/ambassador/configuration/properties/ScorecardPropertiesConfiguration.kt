package com.roche.ambassador.configuration.properties

import com.roche.ambassador.model.ScorecardConfiguration
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScorecardPropertiesConfiguration {

    @Bean
    fun scorecardConfiguration(applicationContext: ApplicationContext): ScorecardConfiguration {
        val binder = Binder.get(applicationContext.environment)
        return binder.bind("ambassador.scorecard", ScorecardConfiguration::class.java)
            .orElseThrow { IllegalStateException("Unable to bind scorecard configuration") }

    }
}