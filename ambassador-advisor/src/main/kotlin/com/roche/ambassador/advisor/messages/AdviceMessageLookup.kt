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
        private const val LINKS_KEY = "links"
    }

    @Cacheable("advice-messages")
    fun get(key: String): AdviceMessage {
        val name = messageSource.getAdviceMessagePart(key, NAME_KEY)
        val reason = messageSource.getAdviceMessagePart(key, REASON_KEY)
        val details = messageSource.getAdviceMessagePart(key, DETAILS_KEY)
        val remediation = messageSource.getAdviceMessagePart(key, REMEDIATION_KEY)
        val priorityStr = messageSource.getAdviceMessagePart(key, PRIORITY_KEY).uppercase()
        val priority = AdviceMessage.AdvicePriority.valueOf(priorityStr)
//        val links = messageSource.getAdviceMessage(key, NAME_KEY)
        return AdviceMessage(
            name = name,
            details = details,
            reason = reason,
            remediation = remediation,
            priority = priority
        )
    }

    private fun MessageSource.getAdviceMessagePart(name: String, key: String): String {
        val fullKey = "$KEY_PREFIX.$name.$key"
        return try {
            getMessage(fullKey, null, Locale.getDefault())
        } catch (e: NoSuchMessageException) {
            log.warn("Missing advice configuration under {} key", fullKey)
            throw AdviceMessageNotFoundException(fullKey, "Missing advice configuration", e)
        }
    }

}