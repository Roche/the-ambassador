package com.roche.ambassador.configuration.i18n

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
            reloadableResourceBundleMessageSource(),
            advicesMessageSource()
        )
        return CompositeMessageSource(messageSources)
    }

    private fun reloadableResourceBundleMessageSource(): MessageSource {
        return createResourceBundleMessageSource().configure {
            it.addBasenames("classpath:i18n/messages", "classpath:i18n/errors", "classpath:i18n/validation")
            it.setCacheSeconds(60 * 30)
        }
    }

    private fun advicesMessageSource(): MessageSource {
        return createResourceBundleMessageSource().configure {
            it.addBasenames("classpath:i18n/advices")
            it.setCacheSeconds(60 * 5)
        }
    }

    private fun createResourceBundleMessageSource(): ReloadableResourceBundleMessageSource {
        return ReloadableResourceBundleMessageSource().configure {
            it.setConcurrentRefresh(true)
            it.setFallbackToSystemLocale(true)
            it.setDefaultLocale(Locale.ENGLISH)
            it.setDefaultEncoding(StandardCharsets.UTF_8.displayName())
        }
    }

    private fun <T : MessageSource> T.configure(configurer: (T) -> Unit): T {
        configurer.invoke(this)
        return this
    }
}
