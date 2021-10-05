package com.filipowm.ambassador.configuration.i18n

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import java.util.*

internal class CompositeMessageSource(
    private val messageSources: List<MessageSource>
) : ReactiveMessageSource {

    companion object {
        private val DEFAULT_LOCALE = Locale.ENGLISH
    }

    override suspend fun getMessage(code: String): String? {
        val locale = ReactiveLocaleContextHolder.getLocaleOrElse(DEFAULT_LOCALE)
        return getMessage(code, null, null, locale)
    }

    override suspend fun getMessage(code: String, defaultMessage: String): String {
        val locale = ReactiveLocaleContextHolder.getLocaleOrElse(DEFAULT_LOCALE)
        return getMessage(code, null, defaultMessage, locale) ?: defaultMessage
    }

    override suspend fun getMessage(code: String, vararg args: Any): String? {
        val locale = ReactiveLocaleContextHolder.getLocaleOrElse(DEFAULT_LOCALE)
        return getMessage(code, args, null, locale)
    }

    override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String? {
        return messageSources.mapNotNull { it.getMessage(code, args, defaultMessage, locale) }
            .firstOrNull()
    }

    override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
        return messageSources.mapNotNull {
            try {
                it.getMessage(code, args, locale)
            } catch (e: NoSuchMessageException) {
                null
            }
        }.firstOrNull() ?: throw NoSuchMessageException(code, locale)
    }

    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
        return messageSources.mapNotNull {
            try {
                it.getMessage(resolvable, locale)
            } catch (e: NoSuchMessageException) {
                null
            }
        }.firstOrNull() ?: throw NoSuchMessageException(resolvable.codes?.joinToString { it } ?: "<unknown>", locale)
    }
}