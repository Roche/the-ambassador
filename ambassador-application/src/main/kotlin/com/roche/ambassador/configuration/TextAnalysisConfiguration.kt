package com.roche.ambassador.configuration

import com.roche.ambassador.document.*
import org.springframework.context.annotation.Bean

// @Configuration
internal open class TextAnalysisConfiguration {

    @Bean
    open fun textAnalyzer(): TextAnalyzingService {
        val acronymDetector = AcronymDetector.none()
        val readabilityTest = ReadabilityTest.none()
        val languageDetector = LanguageDetector.default()
        val props = TextAnalyzerConfiguration(listOf(Language.ENGLISH), 1000)

        return TextAnalyzingService(acronymDetector, languageDetector, readabilityTest, props)
    }
}
