@file:Suppress("Reformat", "Reformat", "Reformat")

package com.filipowm.gitlab.api.model

import java.util.*

enum class AccessLevelName(val value: Int) {
    INVALID(-1),
    NONE(0),
    MINIMAL_ACCESS(5),
    GUEST(10),
    REPORTER(20),
    DEVELOPER(30),

    @Deprecated("deprecated")
    MASTER(40),
    MAINTAINER(40),
    OWNER(50),
    ADMIN(60);

    companion object {
        fun getFromLevel(value: Int): Optional<AccessLevelName> {
            val level = values().firstOrNull { it.value == value }
            return Optional.ofNullable(level)
        }
    }
}
