package com.roche.ambassador.advisor

import com.roche.ambassador.advisor.dsl.Dsl
import com.roche.ambassador.advisor.dsl.RulesBuilder
import com.roche.ambassador.advisor.model.IssueAdvice
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.project.Permissions

object IssueAdvisoryRules {

    fun verify(context: AdvisorContext): IssueAdvice {
        val issueAdvice = IssueAdvice(context.project.name)
        // FIXME rules should be part of model, but temporarily for simplicity are kept here
        Dsl.advise(issueAdvice, context) {
            // @formatter:off
            has { config.visibility.enabled } and { visibility == Visibility.PRIVATE } then "visibility.private"
            whenEnabled(config.description) {
                matchFirst({ description }) {
                    that { isNullOrBlank() } then "description.missing"
                    that { this!!.length < config.description.shortLength } then "description.short"
                }
            }
            has { config.topics.enabled } and { topics.isEmpty() } then "topics.empty"
            whenEnabled(config.forking) {
                createPermissionRule("forking") { forks }
            }
            whenEnabled(config.pullRequest) {
                createPermissionRule("pullrequest") { pullRequests }
            }
            // @formatter:on
        }
        return issueAdvice
    }

    private fun RulesBuilder.createPermissionRule(
        name: String,
        permissionExtractor: Permissions.() -> Permissions.Permission
    ) {
        matchFirst({ permissionExtractor(permissions) }) {
            that { isDisabled() } then "$name.disabled"
            that { canEveryoneAccess() } then "$name.private"
        }
    }
}
