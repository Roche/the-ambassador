package com.roche.ambassador.configuration.web

import com.roche.ambassador.configuration.i18n.ReactiveMessageSource
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.format.FormatterRegistry
import org.springframework.format.datetime.DateFormatter
import org.springframework.format.datetime.DateFormatterRegistrar
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
internal class WebConfiguration : WebFluxConfigurer {

    @Bean
    @Order(-1)
    fun errorWebExceptionHandler(
        reactiveMessageSource: ReactiveMessageSource,
        errorAttributes: ErrorAttributes, webProperties: WebProperties, serverCodecConfigurer: ServerCodecConfigurer, applicationContext: ApplicationContext
    ): ErrorWebExceptionHandler {
        return GlobalErrorWebExceptionHandler(reactiveMessageSource, serverCodecConfigurer, errorAttributes, webProperties, applicationContext)
    }

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(ReactivePageableHandlerMethodArgumentResolver())
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverterFactory(StringToEnumConverter)
        registry.configureDateTimeFormatter()
        registry.configureDateFormatter()
    }

    private fun FormatterRegistry.configureDateTimeFormatter() {
        val registrar = DateTimeFormatterRegistrar()
        registrar.setUseIsoFormat(true)
        registrar.registerFormatters(this)
    }

    private fun FormatterRegistry.configureDateFormatter() {
        val registrar = DateFormatterRegistrar()
        registrar.setFormatter(DateFormatter("yyyy-MM-dd"))
        registrar.registerFormatters(this)
    }

    // FIXME disable CORS until made configurable
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**").allowedOrigins("*").allowedMethods("*").maxAge(3600)
    }
}
