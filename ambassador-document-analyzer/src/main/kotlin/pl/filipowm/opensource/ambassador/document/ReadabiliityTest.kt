package pl.filipowm.opensource.ambassador.document

interface ReadabilityTest {

    fun calculateReadabilityScoreOf(text: String): ReadabilityScore

    companion object : ReadabilityTest {
        fun none() : ReadabilityTest {
            return ReadabilityTest
        }

        override fun calculateReadabilityScoreOf(text: String): ReadabilityScore {
            return ReadabilityScore(0f, ReadabilityScoreRange.NORMAL)
        }
    }
}