package com.roche.ambassador.advisor.templates

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Parser
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.cache.TemplateCache
import com.github.jknack.handlebars.io.TemplateSource
import com.roche.ambassador.advisor.common.AdvisorException
import org.springframework.cache.Cache
import java.io.IOException
import java.util.*

internal class SpringTemplateCache(private val cache: Cache) : TemplateCache {

    override fun clear() = cache.clear()

    override fun evict(source: TemplateSource) = cache.evict(source)

    override fun get(source: TemplateSource, parser: Parser): Template? {
        return Optional.ofNullable(cache.get(source))
            .map { it.get() as Template }
            .orElseGet { parseTemplate(parser, source) }
    }

    private fun parseTemplate(parser: Parser, source: TemplateSource): Template? {
        try {
            return parser.parse(source)
        } catch (ex: IOException) {
            val propagated = Handlebars.Utils.propagate(ex)
            throw AdvisorException("Failed to parse handlebars template at ${source.filename()}", propagated)
        }
    }

    override fun setReload(reload: Boolean): TemplateCache {
        // NOOP
        return this
    }

}