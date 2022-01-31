package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.common.AdvisorException
import com.roche.ambassador.advisor.dsl.AdviceKey
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.advisor.messages.AdviceMessageLookup
import com.roche.ambassador.advisor.model.Advice
import com.roche.ambassador.advisor.model.AdviceData
import com.roche.ambassador.advisor.templates.TemplateEngine
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSource
import com.roche.ambassador.storage.advisor.AdvisoryMessageEntity
import java.time.LocalDateTime
import java.util.*

class AdvisorContext(
    val project: Project,
    val source: ProjectSource,
    private val givenAdvisoryMessages: Map<AdvisoryMessageEntity.Type, List<AdvisoryMessageEntity>>,
    private val adviceMessageLookup: AdviceMessageLookup,
    private val templateEngine: TemplateEngine
) {

    companion object {
        private val log by LoggerDelegate()
        private const val DEFAULT_NAME = "The Ambassador Advice"
    }

    private val advices: MutableSet<GivenAdvisoryMessage> = mutableSetOf()

    fun <T> createAdvice(adviceSimpleName: String, data: AdviceData, details: T? = null): Advice<T> {
        val description: String = templateEngine.process("issue_en.md", data)
        val name = adviceMessageLookup.readRaw("$adviceSimpleName.name")
            .orElse(DEFAULT_NAME)
        val labels = adviceMessageLookup.readRawAsList("$adviceSimpleName.labels")
        val advice = Advice(project.id, adviceSimpleName, name, description, labels, details)
        advices += advice
        return advice
    }

    fun getAdviceConfig(key: AdviceKey): AdviceMessage = adviceMessageLookup.get(key)

    fun markGiven(
        advice: Advice<*>,
        referenceId: Long,
        type: AdvisoryMessageEntity.Type,
        existingAdvisoryMessage: AdvisoryMessageEntity? = null
    ) {
        val advicesToTrack = advices
            .filter { it.advice == advice }
            .filter { it.advisoryMessage == null }
        if (advicesToTrack.size != 1) {
            log.warn("There should be exactly 1 advice to track, found {}. Likely it was already marked.", advicesToTrack.size)
            throw AdvisorException("Unexpected state, there should be exactly 1 advices to track.")
        }
        val advisoryMessage = Optional.ofNullable(existingAdvisoryMessage)
            .orElseGet { createAdvisoryMessageEntity(advice.name, referenceId, type) }
        advisoryMessage.updatedDate = LocalDateTime.now() // only date should change if existing
        advisoryMessage.source = source.name()
        advicesToTrack[0].advisoryMessage = advisoryMessage
    }

    fun getExistingAdvisoryMessagesOfType(type: AdvisoryMessageEntity.Type): List<AdvisoryMessageEntity> {
        return givenAdvisoryMessages.getOrDefault(type, listOf())
    }

    fun getAdvisoryMessages(): List<AdvisoryMessageEntity> = advices.mapNotNull { it.advisoryMessage }

    fun getAdvisoryMessagesCount(): Int = advices.size

    private fun createAdvisoryMessageEntity(name: String, referenceId: Long, type: AdvisoryMessageEntity.Type): AdvisoryMessageEntity {
        return AdvisoryMessageEntity(
            name = name,
            updatedDate = LocalDateTime.now(),
            projectId = project.id,
            source = source.name(),
            referenceId = referenceId,
            type = type
        )
    }

    private operator fun MutableSet<GivenAdvisoryMessage>.plusAssign(advice: Advice<*>) {
        this.add(GivenAdvisoryMessage(advice))
    }

    private data class GivenAdvisoryMessage(val advice: Advice<*>) {
        var advisoryMessage: AdvisoryMessageEntity? = null

        override fun equals(other: Any?): Boolean = if (other is GivenAdvisoryMessage) {
            other.advice == advice
        } else {
            false
        }

        override fun hashCode(): Int = advice.hashCode()
    }
}
