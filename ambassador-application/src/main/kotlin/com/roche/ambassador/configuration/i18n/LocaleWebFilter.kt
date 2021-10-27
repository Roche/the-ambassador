package com.roche.ambassador.configuration.i18n

import org.springframework.context.i18n.LocaleContext
import org.springframework.context.i18n.SimpleLocaleContext
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.server.i18n.LocaleContextResolver
import reactor.core.publisher.Mono
import java.util.*

@Component
internal class LocaleWebFilter(val localeContextResolver: LocaleContextResolver) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return tryGetFromRequest(exchange)
            .switchIfEmpty(ReactiveLocaleContextHolder.getContextMono())
            .switchIfEmpty(Mono.defer {
                val ctx = localeContextResolver.resolveLocaleContext(exchange)
                chain.filter(exchange)
                    .contextWrite(ReactiveLocaleContextHolder.withLocaleContext(Mono.just(ctx)))
                    .then(Mono.empty())
            }).flatMap { chain.filter(exchange).contextWrite(ReactiveLocaleContextHolder.withLocaleContext(Mono.just(it))) }
    }

    private fun tryGetFromRequest(exchange: ServerWebExchange): Mono<LocaleContext> {
        return Optional.ofNullable(exchange.request.queryParams["lang"])
            .filter { it.isNotEmpty() }
            .map { it.first() }
            .map { Locale.forLanguageTag(it) }
            .filter { it.language.isNotBlank() }
            .map { SimpleLocaleContext(it) as LocaleContext }
            .map { Mono.just(it) }
            .orElseGet { Mono.empty() }
    }
}