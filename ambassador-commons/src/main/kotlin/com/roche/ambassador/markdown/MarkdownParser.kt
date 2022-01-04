package com.roche.ambassador.markdown

import com.roche.ambassador.extensions.LoggerDelegate
import com.vladsch.flexmark.parser.Parser
import java.util.*

class MarkdownParser(private val parser: Parser = Parser.builder().build()) {

    companion object {
        private val log by LoggerDelegate()
    }

    fun parse(input: String, name: String = "__unnamed__"): MarkdownDocument {
        val document = parser.parse(input)
        val sb = SectionBuilder()
        val visitor = SectionsBuildingVisitor(sb)
        visitor.visitInternal(document)
        val root = sb.build()
        return MarkdownDocument(root, name)
    }

    fun parseSilently(input: String, name: String): Optional<MarkdownDocument> {
        return try {
            Optional.of(parse(input, name))
        } catch (exc: RuntimeException) {
            log.warn("Error while attempting to parse markdown document", exc)
            Optional.empty()
        }
    }
}
