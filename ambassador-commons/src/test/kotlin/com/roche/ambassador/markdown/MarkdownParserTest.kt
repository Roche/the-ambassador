package com.roche.ambassador.markdown

import io.mockk.every
import io.mockk.spyk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File

class MarkdownParserTest {

    private val parser = MarkdownParser()

    @Test
    fun `should parse empty string`() {
        val doc = parser.parse("", "name")

        assertThat(doc.name).isEqualTo("name")
        assertThat(doc.content.text).isNull()
        assertThat(doc.content.title).isNull()
        assertThat(doc.content.subsections).isEmpty()
        assertThat(doc.content.links).isEmpty()
    }

    @Test
    fun `should not throw exception on any error when parsing silently`() {
        // given parser throwing exception when parsing content
        val spiedParser = spyk<MarkdownParser>()
        every { spiedParser.parse(any(), any()) } throws RuntimeException("parsing error")

        // verify it really throws exception
        assertThrows<RuntimeException> { spiedParser.parse("test", "test") }

        // when
        val result = assertDoesNotThrow { spiedParser.parseSilently("test", "test") }

        // then result is empty
        assertThat(result).isEmpty
    }

    @Test
    fun `should parse links`() {
        // given
        val linksMd = readFile("links.md")

        // when
        val result = parser.parse(linksMd)

        // then
        assertThat(result.content.links)
            .hasSize(3)
            .containsExactly(
                Link("", "http://test.com"),
                Link("named-external", "https://test2"),
                Link("named-relative", "./test"),
            )
    }

    @Test
    fun `should parse heading`() {
        // given
        val headingMd = readFile("heading.md")

        // when
        val result = parser.parse(headingMd)

        // then
        assertThat(result.content.text).isNull()
        assertThat(result.content).hasExactlyOneSection("H1", "C1")
    }

    @Test
    fun `should parse nested sections`() {
        // given
        val nestedMd = readFile("nested.md")

        // when
        val result = parser.parse(nestedMd)

        // then
        assertThat(result.content.text).isNull()
        assertThat(result.content).hasSubsections(
            "H2.1" with "C2.1",
            "H2.2" with "C2.2",
            "H2.3" with "C2.3",
            "H2.4" with "C2.4",
            "H1" with "C1",
        )
        assertThat(result.content)
            .childAt(0)
            .hasSubsections(
                "H2.1.1" with null,
                "H2.1.2" with "C2.1.2"
            )
        assertThat(result.content)
            .childAt(1)
            .hasExactlyOneSection("H2.2.1" with "C2.2.1")
        assertThat(result.content)
            .childAt(2)
            .hasNoSections()
        assertThat(result.content)
            .childAt(3)
            .hasExactlyOneSection("H2.4.1" with null)
        assertThat(result.content)
            .childAt(3).childAt(0)
            .hasExactlyOneSection("H2.4.1.1" with "C2.4.1.1")
        assertThat(result.content)
            .childAt(4)
            .hasNoSections()
    }

    private infix fun String.with(content: String?): Section {
        return Section(this, content)
    }

    @Test
    fun `should parse root with section`() {
        // given
        val rootWithSectionMd = readFile("root_with_section.md")

        // when
        val result = parser.parse(rootWithSectionMd)

        // then
        assertThat(result.content.title).isNull()
        assertThat(result.content.text).isEqualTo("This is root")
        assertThat(result.content).hasExactlyOneSection("H1", "C1")
    }

    @Test
    fun `should parse complex document`() {
        // info: do not verify content here, cause it doesn't make sense. Ensure no exception is thrown and verify subsections titles
        // given
        val rootWithSectionMd = readFile("complex.md")

        // when
        val result = assertDoesNotThrow { parser.parse(rootWithSectionMd) }

        // then
        assertThat(result.content).isNotNull
            .hasSubsectionTitles("Introduction", "Installation",
                                 "Running the tools", "Details on the tools",
                                 "System requirements", "Development", "License")
    }

    private fun readFile(name: String): String {
        return File(javaClass.classLoader.getResource("markdown/$name").file).readText()
    }

    private fun ObjectAssert<Section>.hasSubsections(vararg sections: Section): ObjectAssert<Section> {
        val sectionTuples = sections.map { Tuple(it.title, it.text) }
        extracting { it.subsections.toMutableList() }.asList()
            .extracting("title", "text")
            .containsExactly(*sectionTuples.toTypedArray())
        return this
    }

    private fun ObjectAssert<Section>.hasExactlyOneSection(section: Section): ObjectAssert<Section> {
        return hasSubsections(section)
    }

    private fun ObjectAssert<Section>.hasExactlyOneSection(title: String, text: String? = null): ObjectAssert<Section> {
        return hasExactlyOneSection(title with text)
    }

    private fun ObjectAssert<Section>.hasNoSections(): ObjectAssert<Section> {
        extracting { it.subsections }.asList().isEmpty()
        return this
    }

    private fun ObjectAssert<Section>.hasSubsectionTitles(vararg titles: String?): ObjectAssert<Section> {
        extracting { it.subsections.map(Section::title) }.asList().containsExactly(*titles)
        return this
    }

    private fun ObjectAssert<Section>.childAt(idx: Int): ObjectAssert<Section> {
        return extracting { it.subsections[idx] } as ObjectAssert<Section>
    }

}