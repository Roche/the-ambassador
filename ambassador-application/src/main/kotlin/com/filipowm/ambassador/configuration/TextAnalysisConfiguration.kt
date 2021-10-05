package com.filipowm.ambassador.configuration

import com.filipowm.ambassador.document.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
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
