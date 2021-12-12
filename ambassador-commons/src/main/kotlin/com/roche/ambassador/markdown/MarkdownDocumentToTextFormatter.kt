package com.roche.ambassador.markdown

import java.lang.Appendable
import java.util.*

internal object MarkdownDocumentToTextFormatter {

    private val sectionFormatter = SectionFormatter()

    fun format(document: MarkdownDocument): String {
        val sb = StringBuilder()
        sectionFormatter.format(document.content, sb)
        return sb.toString().trim()
    }

    private class SectionFormatter {
        fun format(section: Section, appendable: Appendable) {
            Optional.ofNullable(section.title)
                .ifPresent { appendable.appendLine(it).appendLine() }
            Optional.ofNullable(section.text)
                .ifPresent { appendable.appendLine(it).appendLine() }

            section.subsections
                .forEach { format(it, appendable) }
        }

    }

}