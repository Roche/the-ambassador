package com.roche.ambassador.markdown

import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.InlineLinkNode
import com.vladsch.flexmark.ast.LinkNode
import com.vladsch.flexmark.ext.emoji.Emoji
import com.vladsch.flexmark.ext.toc.TocBlockBase
import com.vladsch.flexmark.util.ast.*
import java.util.function.BiConsumer

internal class SectionsBuildingVisitor(private val builder: SectionBuilder) : NodeVisitor() {

    private var out: SpaceInsertingSequenceBuilder? = null

    companion object {
        private val unprocessableNodes = listOf(
            DoNotCollectText::class.java,
            Emoji::class.java,
            TocBlockBase::class.java,
        )
    }

    fun visitInternal(node: Node) {
        processNode(node, true, this::visit)
        if (out != null && out!!.isNotEmpty) {
            // flush out buffer
            builder.addContent(out.toString())
        }
    }

    private fun visit(node: Node, handler: Visitor<Node>) {
        handler.visit(node)
    }

    override fun processNode(node: Node, withChildren: Boolean, processor: BiConsumer<Node, Visitor<Node>>) {
        if (!node.isOrDescendantOfType(*unprocessableNodes.toTypedArray())) {
            if (out == null) {
                out = SpaceInsertingSequenceBuilder.emptyBuilder(node.chars)
            }
            if (node is Heading) {
                processHeading(node, processor)
                return
            } else if (node is LinkNode) {
                processLink(node)
            }
            val shouldProcessChildNodes = processText(node)
            if (shouldProcessChildNodes) {
                node.appendLineIfBlankLine()
                processChildren(node, processor)
                node.appendLineIfLineBreak()
            }
        }
    }

    private fun processText(node: Node): Boolean {
        return if (node is TextContainer) {
            node.collectText(out, TextContainer.F_LINK_URL, this)
        } else {
            true
        }
    }

    private fun processLink(node: LinkNode) {
        val url = node.url.toString()
        val title = if (node is InlineLinkNode) {
            node.text
        } else {
            node.title
        }.toString()

        val link = Link(title, url)
        builder.addLink(link)
    }

    private fun processHeading(
        node: Heading,
        processor: BiConsumer<Node, Visitor<Node>>
    ) {
        if (out!!.isNotEmpty) {
            builder.addContent(out.toString())
        }
        out = SpaceInsertingSequenceBuilder.emptyBuilder(node.chars)
        processChildren(node, processor)
        val headerTitle = out.toString()
        builder.addSectionAt(node.level, headerTitle)
        out = null
    }

    private fun Node.appendLineIfBlankLine() {
        if (this is BlankLineBreakNode && out!!.isNotEmpty) {
            out!!.appendEol()
        }
    }

    private fun Node.appendLineIfLineBreak() {
        if (this is LineBreakNode && out!!.needEol()) {
            out!!.appendEol()
        }
    }
}
