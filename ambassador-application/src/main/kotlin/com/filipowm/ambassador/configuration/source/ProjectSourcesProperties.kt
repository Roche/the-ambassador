package com.filipowm.ambassador.configuration.source

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConfigurationProperties("ambassador.source")
@ConstructorBinding
@Validated
data class ProjectSourcesProperties(
    @NotBlank val name: String,
    @NotBlank val url: String,
    @NotBlank val token: String,
    @NotNull val system: System,
    val clientId: String?,
    val clientSecret: String?
) {

    enum class System {
        GITLAB,
        FAKE
    }
}
