package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.common.AdvisorException
import com.roche.ambassador.advisor.configuration.AdvisorProperties
import com.roche.ambassador.advisor.dsl.Dsl
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.advisor.model.Advice
import com.roche.ambassador.advisor.model.IssueAdvice
import com.roche.ambassador.exceptions.AmbassadorException
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.source.Issue
import com.roche.ambassador.storage.advisor.AdvisoryMessageEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
internal class IssueAdvisor(advisorProperties: AdvisorProperties) : Advisor {

    private val issueAdviceGiver: IssueAdviceGiver = IssueAdviceGiver.forMode(advisorProperties.mode)

    companion object {
        private val log by LoggerDelegate()
    }

    private fun prepareIssueAdvice(context: AdvisorContext): IssueAdvice {
        val issueAdvice = IssueAdvice(context.project.name)
        Dsl.advise(issueAdvice, context) {
            // @formatter:off
            has { visibility == Visibility.PRIVATE } then "visibility.private"
            matchFirst {
                has { description.isNullOrBlank() } then "description.missing"
                has { description!!.length < 30 } then "description.short"
            }
            has { topics.isEmpty() } then "topics.empty"
            hasNot { permissions?.canEveryoneFork ?: false } then "forking.disabled"
            hasNot { permissions?.canEveryoneCreatePullRequest ?: false } then "pullrequest.disabled"
            // @formatter:on
        }
        return issueAdvice
    }

    override suspend fun getAdvices(context: AdvisorContext): List<AdviceMessage> {
        val issueAdvice = prepareIssueAdvice(context)
        return issueAdvice.getProblems()
    }

    override suspend fun advise(context: AdvisorContext) {
        val issueAdvice = prepareIssueAdvice(context)
        issueAdviceGiver.giveAdvise(context, issueAdvice)
    }

    private sealed interface IssueAdviceGiver {
        suspend fun giveAdvise(context: AdvisorContext, issueAdvice: IssueAdvice)

        companion object {
            fun forMode(mode: AdvisorProperties.Mode): IssueAdviceGiver {
                return when (mode) {
                    AdvisorProperties.Mode.NORMAL -> NormalIssueGiver
                    AdvisorProperties.Mode.DRY_RUN -> DryRunIssueGiver
                    AdvisorProperties.Mode.DISABLED -> NoOpIssueGiver
                    else -> throw AdvisorException("Unsupported mode: $mode")
                }
            }
        }

        object NormalIssueGiver : IssueAdviceGiver {
            override suspend fun giveAdvise(context: AdvisorContext, issueAdvice: IssueAdvice) {
                val existingAdvisoryMessageToUpdate = resolveAdvisoryMessageToUpdate(context)
                val advice: Advice<*> = context.createAdvice<Unit>("issue", issueAdvice)
                val issue: Issue = advice.asIssue(existingAdvisoryMessageToUpdate?.referenceId)

                val referenceId = if (issueAdvice.getProblems().isEmpty()) {
                    log.info("No problems found for project '{}' (id={})", context.project.name, context.project.id)
                    closeIssueIfExists(existingAdvisoryMessageToUpdate, issue, context)
                } else {
                    createOrUpdateIssue(existingAdvisoryMessageToUpdate, context, issue)
                }
                if (referenceId != null) {
                    context.markGiven(advice, referenceId, AdvisoryMessageEntity.Type.ISSUE, existingAdvisoryMessageToUpdate)
                }
            }

            private suspend fun createOrUpdateIssue(
                existingAdvisoryMessageToUpdate: AdvisoryMessageEntity?,
                context: AdvisorContext,
                issue: Issue
            ): Long {
                try {
                    val sentIssue = if (existingAdvisoryMessageToUpdate == null) {
                        log.info("Creating new issue advice in project {}", issue.projectId)
                        context.source.issues().create(issue)
                    } else {
                        log.info("Updating existing issue advice in project {}", issue.projectId)
                        context.source.issues().update(issue)
                    }
                    return sentIssue.getId()!!
                } catch (ex: AmbassadorException) {
                    log.error("Unable to create issue advice for project '{}' (id={}) in source '{}'", context.project.name, context.project.id, context.source.name(), ex)
                    throw AdvisorException("Failed creating issue advice in source", ex)
                }
            }

            private suspend fun closeIssueIfExists(
                existingAdvisoryMessageToUpdate: AdvisoryMessageEntity?,
                issue: Issue,
                context: AdvisorContext,
            ): Long? {
                if (existingAdvisoryMessageToUpdate != null) {
                    log.info("Closing existing issue {} in project {}", existingAdvisoryMessageToUpdate.referenceId, issue.projectId)
                    val toUpdate = issue.withStatus(Issue.Status.CLOSED)
                    context.source.issues().update(toUpdate)
                    existingAdvisoryMessageToUpdate.closedDate = LocalDateTime.now()
                    return existingAdvisoryMessageToUpdate.referenceId
                }
                return null
            }

            private fun resolveAdvisoryMessageToUpdate(context: AdvisorContext): AdvisoryMessageEntity? {
                val givenIssueAdvisories = context.getExistingAdvisoryMessagesOfType(AdvisoryMessageEntity.Type.ISSUE)
                if (givenIssueAdvisories.isNotEmpty()) {
                    val open = givenIssueAdvisories.filter { it.closedDate == null }
                        .sortedByDescending { it.createdDate }
                    if (open.size > 1) {
                        log.warn("{} ambassador issues are open in project {} (id={})", open.size, context.project.name, context.project.id)
                        // TODO verify in source if is not closed, if yes -- close all with proper comment except latest. And update given advices
                    } else if (open.size == 1) {
                        return open[0]
                    }
                }
                return null
            }

            private fun Advice<*>.asIssue(issueId: Long?): Issue {
                return Issue(issueId, projectId, title, description, labels, Issue.Status.OPEN)
            }
        }

        object DryRunIssueGiver : IssueAdviceGiver {
            override suspend fun giveAdvise(context: AdvisorContext, issueAdvice: IssueAdvice) {
                val sb = StringBuilder("Issue with following messages would be created or updated in project '${issueAdvice.projectName}':\n")
                issueAdvice.getProblems()
                    .forEach {
                        sb += "  - "
                        sb += it.name
                        sb += " -> "
                        sb += it.reason
                        sb += "\n"
                    }
                log.info(sb.toString())
            }

            private operator fun StringBuilder.plusAssign(str: String) {
                this.append(str)
            }
        }

        object NoOpIssueGiver : IssueAdviceGiver {
            override suspend fun giveAdvise(context: AdvisorContext, issueAdvice: IssueAdvice) {
                // do nothing
            }
        }
    }
}
