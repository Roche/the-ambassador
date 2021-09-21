package com.filipowm.ambassador.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@ConfigurationProperties(prefix = "ambassador.indexer")
@ConstructorBinding
@Validated
data class IndexerProperties(
    @NotBlank val lockType: IndexingLockType = IndexingLockType.IN_MEMORY,
    @NestedConfigurationProperty
    @Valid
    val concurrency: ConcurrencyProperties
)

enum class IndexingLockType {
    DATABASE,
    IN_MEMORY
}