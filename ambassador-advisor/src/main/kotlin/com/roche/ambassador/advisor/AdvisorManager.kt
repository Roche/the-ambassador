package com.roche.ambassador.advisor

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
    private val templateEngine: TemplateEngine
) {

    companion object {
        private val log by LoggerDelegate()
        private const val SOURCE = "gitlab" // TODO do not set fixed source for multisource support
    }

    suspend fun giveAdvices(project: Project) {
        log.info("Giving advices for project {} (id={})", project.name, project.id)
        val source = sources.get(SOURCE).orElseThrow()
        val advisoryMessages = advisoryMessageRepository.findAllByProjectIdAndSourceAndClosedDateNull(project.id, SOURCE)
        val context = AdvisorContext(project, source, advisoryMessages.groupBy { it.type }, lookup, templateEngine)
        advisors.forEach {
            it.advise(context)
        }
        advisoryMessageRepository.saveAll(context.getAdvisoryMessages())
        log.info("Project {} (id={}) received {} advisory messages", project.name, project.id, context.getAdvisoryMessagesCount())
    }
}