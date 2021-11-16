package com.roche.ambassador.model.project

import java.time.LocalDateTime

data class ProjectFilter constructor(
    val visibility: Visibility?,
    val archived: Boolean?,
    val lastActivityAfter: LocalDateTime?,
    val groups: List<String> = listOf()
) {

    data class Builder(
        var visibility: Visibility? = null,
        var archived: Boolean? = null,
        var lastActivityAfter: LocalDateTime? = null,
        var groups: MutableSet<String> = mutableSetOf()
    ) {

        fun visibility(visibility: Visibility): Builder = apply { this.visibility = visibility }
        fun private(): Builder = this.visibility(Visibility.PRIVATE)
        fun internal(): Builder = this.visibility(Visibility.INTERNAL)
        fun public(): Builder = this.visibility(Visibility.PUBLIC)
        fun archived(archived: Boolean? = true): Builder = apply { this.archived = archived }
        fun groups(vararg names: String): Builder = apply { groups.addAll(names) }
        fun lastActivityAfter(lastActivityAfter: LocalDateTime?): Builder = apply { this.lastActivityAfter = lastActivityAfter }
        fun build(): ProjectFilter = ProjectFilter(visibility, archived, lastActivityAfter, groups.toList())
    }

    companion object {
        fun all(): ProjectFilter {
            return Builder()
                .archived(false)
                .build()
        }

        fun internal(): ProjectFilter {
            return Builder()
                .internal()
                .archived(false)
                .build()
        }
    }
}
