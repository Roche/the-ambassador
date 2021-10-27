package com.roche.ambassador.document

data class TextAnalyzerConfiguration(
    val supportedLanguages: List<Language>,
    val timeout: Long
)
