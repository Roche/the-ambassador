package com.roche.ambassador.advisor.configuration

data class RulesProperties(
    val visibility: Rule = Rule(true),
    val description: DescriptionRule = DescriptionRule(true, 30),
    val topics: Rule = Rule(),
    val forking: Rule = Rule(),
    val pullRequest: Rule = Rule(),
) {

    open class Rule(
        val enabled: Boolean = true
    )

    class DescriptionRule(enabled: Boolean, val shortLength: Int) : Rule(enabled)
}

