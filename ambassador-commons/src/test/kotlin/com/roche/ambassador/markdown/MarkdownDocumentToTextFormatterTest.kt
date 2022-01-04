package com.roche.ambassador.markdown

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MarkdownDocumentToTextFormatterTest {

    @Test
    fun `should use title if not null`() {
        // given
        val doc = MarkdownDocument(Section("my title"), "test")

        // when
        val result = MarkdownDocumentToTextFormatter.format(doc)

        // then
        assertThat(result).isEqualTo(doc.content.title)
    }

    @Test
    fun `should use text if not null`() {
        // given
        val doc = MarkdownDocument(Section(null, "my text"), "test")

        // when
        val result = MarkdownDocumentToTextFormatter.format(doc)

        // then
        assertThat(result).isEqualTo(doc.content.text)
    }

    @Test
    fun `should process subsections`() {
        val doc = MarkdownDocument(Section("title 1", subsections = listOf(Section("title 2", subsections = listOf(Section("title 3"))))), "test")

        // when
        val result = MarkdownDocumentToTextFormatter.format(doc)

        // then
        assertThat(result).isEqualTo(
            """
        title 1
        
        title 2
        
        title 3
            """.trimIndent()
        )
    }

    @Test
    fun `should process complete document`() {
        val doc = MarkdownDocument(Section("title 1", "text 1", subsections = listOf(Section("title 2", "text 2"))), "test")

        // when
        val result = MarkdownDocumentToTextFormatter.format(doc)

        // then
        assertThat(result).isEqualTo(
            """
        title 1
        
        text 1
        
        title 2
        
        text 2
            """.trimIndent()
        )
    }

    @Test
    fun `should process empty document`() {
        val doc = MarkdownDocument(Section(), "test")

        // when
        val result = MarkdownDocumentToTextFormatter.format(doc)

        // then
        assertThat(result).isEmpty()
    }
}
