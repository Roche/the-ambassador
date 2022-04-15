package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.configuration.AdvisorProperties
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.advisor.messages.AdviceMessageLookup
import com.roche.ambassador.advisor.templates.TemplateEngine
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSources
import com.roche.ambassador.storage.advisor.AdvisoryMessageRepository
import org.springframework.stereotype.Component

@Component
class AdvisorManager(
    private val advisors: List<Advisor>,
    private val advisoryMessageRepository: AdvisoryMessageRepository,
    private val sources: ProjectSources,
    private val lookup: AdviceMessageLookup,
    private val templateEngine: TemplateEngine,
    private val properties: AdvisorProperties
) {

    companion object {
        private val log by LoggerDelegate()
        private const val SOURCE = "gitlab" // TODO do not set fixed source for multisource support
    }

    suspend fun readAdvices(project: Project): List<AdviceMessage> {
        log.info("Reading advices for project {} (id={})", project.name, project.id)
        val context = prepareAdvisorContext(project, false)
        return advisors.flatMap { it.getAdvices(context) }
    }

    suspend fun giveAdvices(project: Project) {
        log.info("Giving advices for project {} (id={})", project.name, project.id)
        val context = prepareAdvisorContext(project, true)
        advisors.forEach {
            it.advise(context)
        }
        advisoryMessageRepository.saveAll(context.getAdvisoryMessages())
        log.info("Project {} (id={}) received {} advisory messages", project.name, project.id, context.getAdvisoryMessagesCount())
    }

    private fun prepareAdvisorContext(project: Project, withExistingAdvisoryMessages: Boolean): AdvisorContext {
        val source = sources.get(SOURCE).orElseThrow()
        val advisoryMessages = if (withExistingAdvisoryMessages) {
            advisoryMessageRepository.findAllByProjectIdAndSourceAndClosedDateNull(project.id, SOURCE)
                .groupBy { it.type }
        } else {
            mapOf()
        }
        return AdvisorContext(project, source, advisoryMessages, lookup, templateEngine, properties.rules)
    }
}
