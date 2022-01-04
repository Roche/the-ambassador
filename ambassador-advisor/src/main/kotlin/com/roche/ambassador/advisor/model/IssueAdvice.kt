package com.roche.ambassador.advisor.model

import com.roche.ambassador.advisor.messages.AdviceMessage

data class IssueAdvice(val projectName: String) : AdviceData, BuildableAdvice {

    private val problems: MutableSet<AdviceMessage> = mutableSetOf()

    override fun apply(message: AdviceMessage) {
        problems.add(message)
    }

    fun getProblems(): List<AdviceMessage> = problems.toList().sortedBy { it.severity.level }
}
