package pl.filipowm.opensource.ambassador.document

import pl.filipowm.innersource.ambassador.model.files.Documentation

data class TextAnalyzingService(val acronymDetector: AcronymDetector,
                                val languageDetector: LanguageDetector,
                                val readabilityTest: ReadabilityTest
) {

    companion object {
        val SUPPORTED_LANGUAGES = listOf(Language.ENGLISH)
    }

    fun analyze(text: String?): Documentation {
        if (text.isNullOrBlank()) {
            return Documentation.notExistent()
        }
        val language = languageDetector.detectLanguageOf(text)
        if (language in SUPPORTED_LANGUAGES) {
            // TODO logging
        }
        val readability = readabilityTest.calculateReadabilityScoreOf(text)
        if (acronymDetector.isAcronym(text)) {

        }
        val hash = Hash.sha256(text)
        return Documentation(language.name, text.length, hash.orElse(""), null, true)
    }

    private fun isAcronymOrAbbreviation(text: String) {
        TODO("acc?")
    }
}
