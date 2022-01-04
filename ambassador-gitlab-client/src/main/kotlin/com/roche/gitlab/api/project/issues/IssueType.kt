package com.roche.gitlab.api.project.issues

import java.util.*

enum class IssueType(val value: String) {
    ISSUE("issue"),
    INCIDENT("incident"),
    TEST_CASE("test_case")
    ;

    companion object {
        fun from(value: String): Optional<IssueType> {
            val level = values().firstOrNull { it.value == value }
            return Optional.ofNullable(level)
        }
    }
}
