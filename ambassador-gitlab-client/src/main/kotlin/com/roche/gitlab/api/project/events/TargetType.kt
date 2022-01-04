package com.roche.gitlab.api.project.events

import java.util.*

enum class TargetType(val value: String) {
    ISSUE("Issue"),
    NOTE("Note"),
    DIFF_NOTE("DiffNote"),
    DISCUSSION_NOTE("DiscussionNote"),
    MERGE_REQUEST("MergeRequest"),
    MILESTONE("Milestone"),
    SNIPPET("Snippet"),
    DESIGN("DesignManagement::Design"),
    WIKI_PAGE("WikiPage::Meta")
    ;
    companion object {
        fun from(value: String): Optional<TargetType> {
            val level = values().firstOrNull { it.value == value }
            return Optional.ofNullable(level)
        }
    }
}
