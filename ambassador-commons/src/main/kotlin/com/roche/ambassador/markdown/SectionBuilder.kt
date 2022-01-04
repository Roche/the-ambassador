package com.roche.ambassador.markdown

class SectionBuilder {

    private var root: BuildableSection = BuildableSection()
    private var currentSection: BuildableSection = root

    fun addContent(content: String): SectionBuilder {
        currentSection.text = content
        return this
    }

    fun addSectionAt(level: Int, title: String): SectionBuilder {
        val parent = currentSection.findByFirstLevelLessThan(level)
        val section = BuildableSection(level, parent, title)
        section.parent!!.subsections.add(section)
        currentSection = section
        return this
    }

    fun addLink(link: Link): SectionBuilder {
        currentSection.links.add(link)
        return this
    }

    fun build(): Section = root.toSection()

    data class BuildableSection(
        var level: Int = 0,
        var parent: BuildableSection? = null,
        var title: String? = null,
        var text: String? = null,
        var subsections: MutableList<BuildableSection> = mutableListOf(),
        var links: MutableList<Link> = mutableListOf(),
    ) {
        fun toSection(): Section = Section(title, text, subsections.map { it.toSection() }, links.toList())

        fun findByFirstLevelLessThan(level: Int): BuildableSection {
            if (this.level < level || parent == null) {
                return this
            }
            return parent!!.findByFirstLevelLessThan(level)
        }

        override fun toString(): String {
            return "%d. %s".format(level, title)
        }
    }
}
