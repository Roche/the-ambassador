package com.roche.ambassador.advisor.templates

import com.github.jknack.handlebars.EscapingStrategy
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.cache.TemplateCache
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class TemplateEngineConfiguration {

    companion object {
        private const val CACHE_NAME = "advisor-templates-cache"
    }

    @Bean
    fun templateEngine(cacheManager: CacheManager): TemplateEngine {
        return TemplateEngine(handlebars(cacheManager))
    }

    private object IfEmpty : Helper<Collection<*>> {
        override fun apply(context: Collection<*>, options: Options): Any {
            return if (context.isEmpty()) {
                options.fn(this)
            } else {
                options.inverse(this)
            }
        }

    }

    fun handlebars(cacheManager: CacheManager): Handlebars {
        return Handlebars(templateLoader())
            .with(templateCache(cacheManager))
            .with(EscapingStrategy.NOOP)
            .registerHelper("ifEmpty", IfEmpty)
            .prettyPrint(true)
    }

    private fun templateLoader(): TemplateLoader {
        return ClassPathTemplateLoader("/templates", "")
    }

    private fun templateCache(cacheManager: CacheManager): TemplateCache {
        return SpringTemplateCache(cacheManager.getCache(CACHE_NAME)!!)
    }
}
