package com.filipowm.ambassador.configuration.properties

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class AsyncProperties(
    @Min(1) val corePoolSize: Int = 2,
    @Min(1) val maxPoolsSize: Int = 10,
    @NotBlank val threadNamePrefix: String? = null
)