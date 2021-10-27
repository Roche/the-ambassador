package com.roche.ambassador.model.criteria

class OrCriterion<T>(
    private val first: Criterion<T>,
    private val second: Criterion<T>
) : Criterion<T> {

    private var isFirstSuccessful = false
    private var isSecondSuccessful = false

    override fun getFailureMessage(input: T): String {
        return if (isFirstSuccessful && isSecondSuccessful) {
            """Both criteria failed:
               1. ${first.getFailureMessage(input)}
               2. ${second.getFailureMessage(input)}
            """.trimIndent()
        } else {
            throw IllegalStateException("Criteria were not evaluated, unable to determine failure message")
        }
    }

    override fun evaluate(input: T): Boolean {
        isFirstSuccessful = first.evaluate(input)
        isSecondSuccessful = second.evaluate(input)
        return isFirstSuccessful || isSecondSuccessful
    }
}