package pl.filipowm.opensource.ambassador.configuration.source

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties("ambassador.source")
@Validated
class ProjectSourceProperties {

    @NotBlank
    var url: String? = null
    @NotBlank
    var token: String? = null
    @NotNull
    var system : System? = null

    enum class System {
        GITLAB
    }
}