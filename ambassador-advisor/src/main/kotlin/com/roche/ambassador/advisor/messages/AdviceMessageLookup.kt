package com.roche.ambassador.advisor.messages

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.*

// TODO for first version use message source, then move to DB when structure is stable
@Component
class AdviceMessageLookup(private val messageSource: MessageSource) {

    companion object {
        private val log by LoggerDelegate()
        private const val KEY_PREFIX = "advice"
        private const val NAME_KEY = "name"
        private const val REASON_KEY = "reason"
        private const val DETAILS_KEY = "details"
        private const val REMEDIATION_KEY = "remediation"
        private const val PRIORITY_KEY = "priority"
    }

    fun readRaw(key: String): Optional<String> {
        return try {
            Optional.of(messageSource.getAdviceMessagePart(key))
        } catch (e: AdviceMessageNotFoundException) {
            Optional.empty()
        }
    }

    fun readRawAsList(key: String): List<String> {
        return readRaw(key)
            .map { it.split(",") }
            .orElseGet { listOf() }
            .map { it.trim() }
    }

    @Cacheable("advice-messages")
    fun get(key: String): AdviceMessage {
        val name = messageSource.getAdviceMessagePart(key, NAME_KEY)
        val reason = messageSource.getAdviceMessagePart(key, REASON_KEY)
        val details = messageSource.getAdviceMessagePart(key, DETAILS_KEY)
        val remediation = messageSource.getAdviceMessagePart(key, REMEDIATION_KEY)
        val priorityStr = messageSource.getAdviceMessagePart(key, PRIORITY_KEY).uppercase()
        val priority = AdviceMessage.AdvicePriority.valueOf(priorityStr)
        return AdviceMessage(
            name = name,
            details = details,
            reason = reason,
            remediation = remediation,
            priority = priority
        )
    }

    private fun StringJoiner.addIfNotNullOrEmpty(newElement: CharSequence?): StringJoiner {
        if (newElement != null && newElement.isNotBlank()) {
            return add(newElement)
        }
        return this
    }

    private fun MessageSource.getAdviceMessagePart(vararg parts: String): String {
        val sj = StringJoiner(".")
        sj.add(KEY_PREFIX)
        parts.forEach { sj.addIfNotNullOrEmpty(it) }
        val fullKey = sj.toString()
        return try {
            getMessage(fullKey, null, Locale.getDefault())
        } catch (e: NoSuchMessageException) {
            log.warn("Missing advice configuration under {} key", fullKey)
            throw AdviceMessageNotFoundException(fullKey, "Missing advice configuration", e)
        }
    }

}