package com.filipowm.ambassador.document

open class TextAnalysisResult(
    val sentences: Int?,
    val uniqueSentences: Int?,
    val words: Int,
    val uniqueWords: Int,
    val complexWords: Int?,
    val syllabes: Int,
    val characters: Int
)
