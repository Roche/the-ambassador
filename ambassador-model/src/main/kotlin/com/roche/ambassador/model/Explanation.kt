package com.roche.ambassador.model

interface Explanation {

    fun description(): String?
    fun value(): Double?
    fun maxValue(): Double?
    fun details(): List<Explanation>

    fun isPresent(): Boolean = value() != null || description() != null || details().isNotEmpty()

    companion object {
        fun no(description: String): Explanation = NoExplanation(description)
        fun noValue(description: String): Explanation = NoExplanation(description)
        fun calculated(description: String, value: Double, maxValue: Double? = null, vararg pieces: Any): Explanation =
            CalculatedExplanation(description, value, maxValue, *pieces)
    }
}

private abstract class AbstractExplanation(
    private val description: String? = null,
    private val value: Double?,
    private val maxValue: Double?,
    private val details: List<Explanation> = listOf()
) : Explanation {

    override fun description() = description

    override fun value() = value

    override fun maxValue() = maxValue

    override fun details() = details

}

private class NoExplanation(description: String) : AbstractExplanation(description, null, null) {
    override fun isPresent() = false
}

private class CalculatedExplanation(description: String, value: Double, maxValue: Double? = null, vararg pieces: Any) :
    AbstractExplanation(description.replace("{}", "%s").format(pieces), value, maxValue)