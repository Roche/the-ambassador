package com.roche.ambassador.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import java.time.Duration
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ConfigurationProperties(prefix = "ambassador.indexer")
@ConstructorBinding
@Validated
data class IndexerProperties(
    @NotBlank
    val lockType: IndexingLockType = IndexingLockType.IN_MEMORY,

    @Min(0)
    val historySize: Int = 10,

    @NotNull
    val gracePeriod: Duration = Duration.ofDays(7),

    @NestedConfigurationProperty
    @Valid
    val concurrency: ConcurrencyProperties = ConcurrencyProperties(),

    @NestedConfigurationProperty
    @Valid
    val criteria: IndexingCriteriaProperties = IndexingCriteriaProperties(),

    @NestedConfigurationProperty
    @Valid
    val features: FeaturesReadingProperties = FeaturesReadingProperties(),

    @NestedConfigurationProperty
    @Valid
    val subscription: SubscriptionProperties = SubscriptionProperties()

)

enum class IndexingLockType {
    DATABASE,
    IN_MEMORY
}
