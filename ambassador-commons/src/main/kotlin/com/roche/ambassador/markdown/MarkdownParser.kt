package com.roche.ambassador.markdown

import com.vladsch.flexmark.parser.Parser
import java.util.*

class MarkdownParser(private val parser: Parser = Parser.builder().build()) {

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
            Optional.empty()
        }
    }

}