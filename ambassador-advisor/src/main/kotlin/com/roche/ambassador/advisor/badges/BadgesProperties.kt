package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.common.Color
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "ambassador.badges")
@ConstructorBinding
@Validated
internal data class BadgesProperties(
    val provider: ProviderType = ProviderType.SHIELDS,
    val config: Map<String, String>,
    private val colors: Map<Color, String>
) {

    @AllColorsDefined
    fun getColors(): Map<Color, String> {
        return colors
    }

    enum class ProviderType {
        SHIELDS,
        TEXT
    }
}
