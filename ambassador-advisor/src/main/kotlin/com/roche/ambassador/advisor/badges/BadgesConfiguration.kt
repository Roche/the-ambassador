package com.roche.ambassador.advisor.badges

import com.roche.ambassador.advisor.templates.TemplateEngine
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BadgesProperties::class)
internal class BadgesConfiguration {

    @Bean
    fun badgeProvider(badgesProperties: BadgesProperties, templateEngine: TemplateEngine): BadgeProvider {
        return when(badgesProperties.provider) {
           BadgesProperties.ProviderType.SHIELDS -> ShieldsBadgeProvider(badgesProperties.config, colorResolver(badgesProperties))
           BadgesProperties.ProviderType.TEXT -> TextBadgeProvider(badgesProperties.config, templateEngine)
        }
    }

    private fun colorResolver(badgesProperties: BadgesProperties): ColorResolver {
        return ColorResolver(badgesProperties.getColors())
    }

}