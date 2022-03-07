package com.roche.ambassador.configuration.web

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.format.FormatterRegistry
import org.springframework.format.datetime.DateFormatter
import org.springframework.format.datetime.DateFormatterRegistrar
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import java.time.format.DateTimeFormatter

@Configuration
internal class WebConfiguration : WebFluxConfigurer {

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
        corsRegistry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3600)
    }
}
