package com.filipowm.ambassador.configuration.i18n

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.context.i18n.LocaleContext
import org.springframework.context.i18n.SimpleLocaleContext
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.util.*
import java.util.function.Function

object ReactiveLocaleContextHolder {

    private val LOCALE_CONTEXT_KEY: Class<*> = LocaleContext::class.java

    fun getContextMono(): Mono<LocaleContext?> {
        return Mono.subscriberContext()
            .filter(this::hasLocaleContext)
            .flatMap(this::getLocaleContext)
            .switchIfEmpty(Mono.empty())
    }

    suspend fun getContext(): Optional<LocaleContext> = Optional.ofNullable(getContextMono().awaitFirstOrNull())

    suspend fun getLocale(): Optional<Locale> = getContext().map { it.locale }

    suspend fun getLocaleOrElse(default: Locale): Locale = getLocale().orElse(default)

    private fun hasLocaleContext(context: Context): Boolean {
        return context.hasKey(LOCALE_CONTEXT_KEY)
    }

    private fun getLocaleContext(context: Context): Mono<LocaleContext?>? {
        return context.get<Mono<LocaleContext?>>(LOCALE_CONTEXT_KEY)
    }

    fun clearContext(): Function<Context, Context> {
        return Function { context: Context -> context.delete(LOCALE_CONTEXT_KEY) }
    }

    fun withLocaleContext(LocaleContext: Mono<LocaleContext>): Context {
        return Context.of(LOCALE_CONTEXT_KEY, LocaleContext)
    }

    fun withLocale(locale: Locale?): Context {
        return withLocaleContext(Mono.just(SimpleLocaleContext(locale)))
    }
}
