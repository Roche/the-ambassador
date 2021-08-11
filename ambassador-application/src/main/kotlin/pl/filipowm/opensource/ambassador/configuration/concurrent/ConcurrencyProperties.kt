package pl.filipowm.opensource.ambassador.configuration.concurrent

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "ambassador.indexer.concurrency")
@ConstructorBinding
@Validated
data class ConcurrencyProperties(
    @Min(2) val concurrencyLevel: Int = 10,
    @NotBlank val producerThreadPrefix: String = "indxr-",
    @NotBlank val consumerThreadPrefix: String = "prj-indxr-"
)