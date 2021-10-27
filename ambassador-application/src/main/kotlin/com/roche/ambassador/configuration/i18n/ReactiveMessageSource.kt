package com.roche.ambassador.configuration.i18n

import org.springframework.context.MessageSource

interface ReactiveMessageSource : MessageSource {

    suspend fun getMessage(code: String): String?
    suspend fun getMessage(code: String, defaultMessage: String): String
    suspend fun getMessage(code: String, vararg args: Any): String?

}