package com.roche.ambassador.markdown

data class MarkdownDocument(
    val content: Section,
    val name: String,
) {

    fun asText(): String {
        return MarkdownDocumentToTextFormatter.format(this)
    }
}
