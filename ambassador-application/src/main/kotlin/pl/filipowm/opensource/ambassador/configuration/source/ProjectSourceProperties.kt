package pl.filipowm.opensource.ambassador.configuration.source

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConfigurationProperties("ambassador.source")
@ConstructorBinding
@Validated
data class ProjectSourceProperties(
    @NotBlank val url: String,
    @NotBlank val token: String,
    @NotNull val system: System
) {

    enum class System {
        GITLAB
    }
}