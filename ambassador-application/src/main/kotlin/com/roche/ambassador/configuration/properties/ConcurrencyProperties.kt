package com.roche.ambassador.configuration.properties

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class ConcurrencyProperties(
    @Min(2) val concurrencyLevel: Int = 10,
    @NotBlank val producerThreadPrefix: String = "indxr-",
    @NotBlank val consumerThreadPrefix: String = "prj-indxr-"
)
