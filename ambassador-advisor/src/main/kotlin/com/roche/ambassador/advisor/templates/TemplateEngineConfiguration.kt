package com.roche.ambassador.advisor.templates

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.TemplateCache
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal open class TemplateEngineConfiguration {

    companion object {
        private const val CACHE_NAME = "advisor-templates-cache"
    }

    @Bean
    fun templateEngine(cacheManager: CacheManager): TemplateEngine {
        return TemplateEngine(handlebars(cacheManager))
    }

    fun handlebars(cacheManager: CacheManager): Handlebars {
        return Handlebars(templateLoader())
            .with(templateCache(cacheManager))
            .prettyPrint(true)
    }

    private fun templateLoader(): TemplateLoader {
        return ClassPathTemplateLoader("/templates", "")
    }

    private fun templateCache(cacheManager: CacheManager): TemplateCache {
        return SpringTemplateCache(cacheManager.getCache(CACHE_NAME)!!)
    }
}