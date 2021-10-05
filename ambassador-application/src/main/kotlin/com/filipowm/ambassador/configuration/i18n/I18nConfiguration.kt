package com.filipowm.ambassador.configuration.i18n

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets
import java.util.*

@Configuration
internal class I18nConfiguration {

    @Bean
    fun messageSource(): MessageSource {
        val messageSources = listOf(
            reloadableResourceBundleMessageSource()
        )
        return CompositeMessageSource(messageSources)
    }

    private fun reloadableResourceBundleMessageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setConcurrentRefresh(true)
        messageSource.setBasenames("classpath:i18n/messages", "classpath:i18n/errors", "classpath:i18n/validation")
        messageSource.setFallbackToSystemLocale(true)
        messageSource.setDefaultLocale(Locale.ENGLISH)
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName())
        return messageSource
    }
}