package com.roche.ambassador.markdown

import com.fasterxml.jackson.annotation.JsonIgnore

data class Section(
    val title: String? = null,
    val text: String? = null,
    val subsections: List<Section> = listOf(),
    val links: List<Link> = listOf(),
) {

    @JsonIgnore
    fun isEmpty(): Boolean = title == null && text == null && subsections.isEmpty() && links.isEmpty()
}
