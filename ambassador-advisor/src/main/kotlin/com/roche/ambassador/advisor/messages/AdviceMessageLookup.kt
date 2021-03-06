package com.roche.ambassador.advisor.messages

import com.roche.ambassador.advisor.badges.Badge
import com.roche.ambassador.advisor.badges.BadgeProvider
import com.roche.ambassador.advisor.dsl.AdviceKey
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.extensions.addIfNotNullOrEmpty
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.stereotype.Component
import java.util.*

// TODO for first version use message source, then move to DB when structure is stable
@Component
class AdviceMessageLookup(
    private val messageSource: MessageSource,
    private val badgeProvider: BadgeProvider
) {

    companion object {
        private val log by LoggerDelegate()
        private const val KEY_PREFIX = "advice"
        private const val REASON_KEY = "reason"
        private const val DETAILS_KEY = "details"
        private const val REMEDIATION_KEY = "remediation"
        private const val SEVERITY_KEY = "severity"
    }

    fun readRaw(key: String): Optional<String> {
        return try {
            Optional.of(messageSource.getAdviceMessagePart(AdviceKey(key)))
        } catch (e: AdviceMessageNotFoundException) {
            log.warn("Missing advice message under {} key", key)
            Optional.empty()
        }
    }

    fun readRawAsList(key: String): List<String> {
        return readRaw(key)
            .map { it.split(",") }
            .orElseGet { listOf() }
            .map { it.trim() }
    }

    fun get(key: AdviceKey): Optional<AdviceMessage> {
        try {
            val reason = messageSource.getAdviceMessagePart(key, REASON_KEY)
            val details = messageSource.getAdviceMessagePart(key, DETAILS_KEY)
            val remediation = messageSource.getAdviceMessagePart(key, REMEDIATION_KEY)
            val severityStr = messageSource.getAdviceMessagePart(key, SEVERITY_KEY).uppercase()
            val severity = AdviceMessage.AdviceSeverity.valueOf(severityStr)
            val message = AdviceMessage(
                name = key.key,
                details = details,
                reason = reason,
                remediation = remediation,
                severity = severity,
                severityBadge = badgeProvider.getBadgeAsMarkdown(Badge(severityStr, label = "", color = severity.color))
            )
            return Optional.of(message)
        } catch (e: AdviceMessageNotFoundException) {
            return Optional.empty()
        }
    }

    private fun MessageSource.getAdviceMessagePart(adviceKey: AdviceKey, vararg parts: String): String {
        val sj = StringJoiner(".")
        sj.add(KEY_PREFIX)
        sj.addIfNotNullOrEmpty(adviceKey.key)
        parts.forEach { sj.addIfNotNullOrEmpty(it) }
        val fullKey = sj.toString()
        return try {
            getMessage(fullKey, adviceKey.params.toTypedArray(), Locale.getDefault())
        } catch (e: NoSuchMessageException) {
            log.warn("Missing advice configuration under {} key", fullKey)
            throw AdviceMessageNotFoundException(fullKey, "Missing advice configuration", e)
        }
    }
}
