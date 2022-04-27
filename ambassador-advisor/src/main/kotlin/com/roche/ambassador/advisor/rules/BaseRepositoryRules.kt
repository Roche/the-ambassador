package com.roche.ambassador.advisor.rules

import com.roche.ambassador.advisor.dsl.RulesBuilder
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.feature.PullRequestsFeature
import com.roche.ambassador.model.project.Project

object BaseRepositoryRules : RepositoryRule {

    private fun Project.getCloseSpeed(): String {
        return features.findValue(PullRequestsFeature::class)
            .map { it.averageTimeToMergeAsString() }
            .orElse("0s")
    }

    override fun RulesBuilder.apply() {
        whenEnabled(config.pullRequest) {
            matchFirst(PullRequestsFeature::class, { averageTimeToMergeAsSeconds() }) {
                that { this >= config.pullRequest.closeSpeed.high } then "pullrequest.close-speed.critical" with { listOf(getCloseSpeed(), config.pullRequest.closeSpeed.high.toHumanReadable()) }
                that { this >= config.pullRequest.closeSpeed.med } then "pullrequest.close-speed.very-low" with { listOf(getCloseSpeed(), config.pullRequest.closeSpeed.med.toHumanReadable()) }
                that { this >= config.pullRequest.closeSpeed.low } then "pullrequest.close-speed.low" with { listOf(getCloseSpeed(), config.pullRequest.closeSpeed.low.toHumanReadable()) }
            }
        }

        matchFirst({ stats.repositorySize ?: -1 }) {
            that { this >= config.repository.size.high } then "size.repository.high" with { listOf(stats.repositorySize!!, config.repository.size.high) }
            that { this >= config.repository.size.med } then "size.repository.medium" with { listOf(stats.repositorySize!!, config.repository.size.med) }
            that { this >= config.repository.size.low } then "size.repository.low" with { listOf(stats.repositorySize!!, config.repository.size.low) }
        }

        createPermissionBasedRuleset("forking", config.forking) { forks }
        createPermissionBasedRuleset("pullrequest", config.pullRequest) { pullRequests }
    }
}