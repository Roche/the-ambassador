package com.roche.ambassador.advisor.rules

import com.roche.ambassador.advisor.dsl.RulesBuilder

object ProjectConfigurationRules : Rule {
    override fun RulesBuilder.apply() {
        whenEnabled(config.description) {
            matchFirst({ description }) {
                that { isNullOrBlank() } then "description.missing" with { config.description.shortLength }
                that { this!!.length < config.description.shortLength } then "description.short" with { listOf(description!!.length, config.description.shortLength)  }
            }
        }
        whenEnabled(config.topics) {
            matchFirst({ topics }) {
                that { isEmpty() } then "topics.missing"
                that { size < config.topics.recommendedCount } then "topics.too-few" with { listOf(topics.size, config.topics.recommendedCount) }
            }
        }

        createPermissionBasedRuleset("issues", config.issues) { issues }
    }
}