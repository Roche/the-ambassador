package com.roche.ambassador.configuration.properties

import org.springframework.boot.context.properties.NestedConfigurationProperty

data class SubscriptionProperties(
    @NestedConfigurationProperty val unsubscribe: Unsubscribe = Unsubscribe()
) {

    data class Unsubscribe(
        val topic: String? = null,
        val filename: String? = null
    )

}