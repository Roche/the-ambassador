package com.filipowm.ambassador.model

interface Explanation {

    fun name(): String
    fun description(): String?
    fun value(): Double?
    fun maxValue(): Double?
    fun details(): List<Explanation>

    fun isPresent(): Boolean = value() != null || description() != null || details().isNotEmpty()

    companion object {
        fun no(name: String): Explanation = NoExplanation(name)
//        fun single(explanation: String): Explanation = SingleExplanation(explanation)
//        fun multiple(vararg explanation: String): Explanation = MultipleExplanation(*explanation)
    }
}

private abstract class AbstractExplanation(
    private val name: String, private val description: String? = null,
    private val value: Double?, private val maxValue: Double?, private val details: List<Explanation> = listOf()
) : Explanation {
    override fun name() = name

    override fun description() = description

    override fun value() = value

    override fun maxValue() = maxValue

    override fun details() = details

}

private class NoExplanation(name: String, description: String? = null) : AbstractExplanation(name, description, null, null) {
    override fun isPresent() = false
}

//private class SingleExplanation(private val explanation: String?) : Explanation {
//    override fun details(): String? = explanation
//}
//
//private class MultipleExplanation(vararg explanations: String) : Explanation {
//
//    private val explanations = explanations.asList()
//
//    override fun details(): String = explanations.joinToString(", ")
//
//    override fun isPresent() = explanations.isNotEmpty()
//
//}