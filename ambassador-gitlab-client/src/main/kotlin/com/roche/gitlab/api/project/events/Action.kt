package com.roche.gitlab.api.project.events

import java.util.*

enum class Action(val value: String) {
    ADDED("added"),
    OPENED("opened"),
    CLOSED("closed"),
    DELETED("deleted"),
    ACCEPTED("accepted"),
    PUSHED_TO("pushed to"),
    PUSHED_NEW("pushed new"),
    REMOVED("removed"),
    LEFT("left"),
    REMOVED_DUE_MEMBERSHIP_EXPIRATION("removed due to membership expiration from"),
    COMMENTED_ON("commented on")
    ;
    companion object {
        fun from(value: String): Optional<Action> {
            val level = values().firstOrNull { it.value == value }
            return Optional.ofNullable(level)
        }
    }
}
/*
"wiki_page": {
"format": "markdown",
"slug": "home",
"title": "home"
},
 */
