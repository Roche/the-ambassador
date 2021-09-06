package pl.filipowm.opensource.ambassador.document

import java.util.*

interface AcronymDetector {

    fun isAcronym(text: String): Boolean
    fun extractAcronymDefinitionsFrom(text: String): List<Acronym>
    fun extractClosestAcronymFrom(text: String): Optional<Acronym>

    companion object : AcronymDetector {
        fun none(): AcronymDetector {
            return AcronymDetector
        }

        override fun isAcronym(text: String): Boolean {
            return false
        }

        override fun extractAcronymDefinitionsFrom(text: String): List<Acronym> {
            return emptyList()
        }

        override fun extractClosestAcronymFrom(text: String): Optional<Acronym> {
            return Optional.empty()
        }
    }
}
