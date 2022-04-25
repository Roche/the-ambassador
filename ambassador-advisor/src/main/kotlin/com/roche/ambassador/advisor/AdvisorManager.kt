package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.configuration.AdvisorProperties
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.advisor.messages.AdviceMessageLookup
import com.roche.ambassador.advisor.templates.TemplateEngine
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.source.ProjectSources
import com.roche.ambassador.storage.advisor.AdvisoryMessageRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class AdvisorManager(
    private val advisors: List<Advisor>,
    private val advisoryMessageRepository: AdvisoryMessageRepository,
    private val sources: ProjectSources,
    private val lookup: AdviceMessageLookup,
    private val templateEngine: TemplateEngine,
    private val properties: AdvisorProperties
) {

    private val advisorEligibilityVerifier: AdvisorEligibilityVerifier

    companion object {
        private val log by LoggerDelegate()
        private const val SOURCE = "gitlab" // TODO do not set fixed source for multisource support
    }

    init {
        val verifiers = mutableListOf<AdvisorEligibilityVerifier>()
        if (properties.enabledForGroups.isNotEmpty()) {
            verifiers += GroupBasedEligibilityVerifier(properties.enabledForGroups)
        }
        if (properties.enabledForProjects.isNotEmpty()) {
            verifiers += ProjectBasedEligibilityVerifier(properties.enabledForProjects)
        }
        advisorEligibilityVerifier = if (verifiers.size > 0) {
            AnyOfEligibilityVerifier(verifiers)
        } else if (!properties.enabledForPrivateProjects) {
            PrivateProjectEligibilityVerifier()
        } else {
            AlwaysTrueEligibilityVerifier()
        }
    }

    @Transactional(readOnly = true)
    open suspend fun readAdvices(project: Project): List<AdviceMessage> {
        log.info("Reading advices for project {} (id={})", project.name, project.id)
        val context = prepareAdvisorContext(project, false)
        return advisors.flatMap { it.getAdvices(context) }
    }

    @Transactional
    open suspend fun giveAdvices(project: Project) {
        if (advisorEligibilityVerifier.isEligible(project)) {
            log.info("Giving advices for project {} (id={})", project.name, project.id)
            val context = prepareAdvisorContext(project, true)
            advisors.forEach {
                it.advise(context)
            }
            advisoryMessageRepository.saveAll(context.getAdvisoryMessages())
            log.info("Project {} (id={}) received {} advisory messages", project.name, project.id, context.getAdvisoryMessagesCount())
        } else {
            log.info("Project {} (id={}) is not eligible for advisory", project.name, project.id)
        }
    }

    private fun prepareAdvisorContext(project: Project, withExistingAdvisoryMessages: Boolean): AdvisorContext {
        val source = sources.get(SOURCE).orElseThrow()
        val advisoryMessages = if (withExistingAdvisoryMessages) {
            advisoryMessageRepository.findAllByProjectIdAndSourceAndClosedDateNull(project.id, SOURCE).groupBy { it.type }
        } else {
            mapOf()
        }
        return AdvisorContext(project, source, advisoryMessages, lookup, templateEngine, properties.rules)
    }

    private class AlwaysTrueEligibilityVerifier : AdvisorEligibilityVerifier {
        override fun isEligible(project: Project): Boolean = true
    }

    private class AnyOfEligibilityVerifier(private val verifiers: List<AdvisorEligibilityVerifier>) : AdvisorEligibilityVerifier {
        override fun isEligible(project: Project): Boolean = verifiers.any { it.isEligible(project) }
    }

    private class ProjectBasedEligibilityVerifier(private val enabledForProjects: Set<String>) : AdvisorEligibilityVerifier {
        override fun isEligible(project: Project): Boolean {
            val projectId = project.id.toString()
            return enabledForProjects.any { it == projectId || it.equals(project.fullName, ignoreCase = true) }
        }
    }

    private class PrivateProjectEligibilityVerifier : AdvisorEligibilityVerifier {
        override fun isEligible(project: Project): Boolean = project.visibility != Visibility.PRIVATE
    }

    private class GroupBasedEligibilityVerifier(private val enabledForGroups: Set<String>) : AdvisorEligibilityVerifier {
        override fun isEligible(project: Project): Boolean {
            val group = project.parent
            return if (group != null) {
                val groupId = group.id.toString()
                enabledForGroups.any { it == groupId || it.equals(group.fullName, ignoreCase = true) }
            } else {
                false
            }
        }
    }

    private interface AdvisorEligibilityVerifier {
        fun isEligible(project: Project): Boolean
    }
}
