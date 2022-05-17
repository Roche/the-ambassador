package com.roche.ambassador.advisor.rules

import com.roche.ambassador.advisor.AdvisorContext
import com.roche.ambassador.advisor.dsl.Dsl
import com.roche.ambassador.advisor.dsl.RulesBuilder
import com.roche.ambassador.advisor.model.IssueAdvice
import com.roche.ambassador.model.Visibility

class AdvisoryRules(
    private val ciRules: List<CiRule>,
    private val repositoryRules: List<RepositoryRule>,
    private val otherRules: List<Rule>
) {

    fun verify(context: AdvisorContext): IssueAdvice {
        val issueAdvice = IssueAdvice(context.project.name)
        // FIXME rules should be part of model, but temporarily for simplicity are kept here
        Dsl.advise(issueAdvice, context) {
            // @formatter:off
            matchFirst {
                has { visibility == Visibility.PRIVATE } then "visibility.private"
                or {
                    applyOnNonPrivate()
                }
            }
            // @formatter:on
        }
        return issueAdvice
    }

    private fun RulesBuilder.applyOnNonPrivate() {
        otherRules.apply(this)
        repositoryRules.apply(this)
        createPermissionBasedRuleset("ci", config.ci, ciRules) { ci }
    }

}