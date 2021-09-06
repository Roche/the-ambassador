package com.filipowm.ambassador.model.project

import java.time.LocalDateTime

data class ProjectFilter constructor(
    val visibility: Visibility?,
    val archived: Boolean?,
    val lastActivityAfter: LocalDateTime?
) {

    data class Builder(
        var visibility: Visibility? = null,
        var archived: Boolean? = null
    ) {

        fun visibility(visibility: Visibility) = apply { this.visibility = visibility }
        fun private() = this.visibility(Visibility.PRIVATE)
        fun internal() = this.visibility(Visibility.INTERNAL)
        fun public() = this.visibility(Visibility.PUBLIC)
        fun archived(archived: Boolean? = true) = apply { this.archived = archived }
        fun build() = ProjectFilter(visibility, archived, null)
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
