package com.roche.ambassador.advisor.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "ambassador.advisor")
@ConstructorBinding
@Validated
data class AdvisorProperties(
    val mode: Mode = Mode.NORMAL
) {

    fun isEnabled(): Boolean = mode != Mode.DISABLED

    fun isDryRun(): Boolean = mode == Mode.DRY_RUN

    enum class Mode {
        DISABLED,
        DRY_RUN,
        NORMAL
    }
}