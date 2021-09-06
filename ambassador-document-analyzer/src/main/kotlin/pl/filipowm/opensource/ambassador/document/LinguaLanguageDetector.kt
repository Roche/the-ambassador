package pl.filipowm.opensource.ambassador.document

import com.github.pemistahl.lingua.api.Language as LinguaLanguage
import com.github.pemistahl.lingua.api.LanguageDetector as DelegateLinguaLanguageDetector

class LinguaLanguageDetector(private val delegate: DelegateLinguaLanguageDetector) : LanguageDetector {

    override fun warmUp() {
        detectLanguageOf("this is a warm up")
    }

    override fun detectLanguageOf(text: String): Language {
        val linguaLang = delegate.detectLanguageOf(text)
        return matchLanguage(linguaLang)
    }

    private fun matchLanguage(language: LinguaLanguage): Language {
        return try {
            enumValueOf(language.name)
        } catch (e: IllegalArgumentException) {
            Language.UNKNOWN
        }
    }
}
