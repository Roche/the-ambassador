package com.roche.ambassador.model.project

class Permissions(
    val ci: Permission,
    val containerRegistry: Permission,
    val forks: Permission,
    val issues: Permission,
    val pullRequests: Permission,
    val repository: Permission,
) {

    enum class Permission {
        PUBLIC,
        PRIVATE,
        DISABLED,
        UNKNOWN,
        ;

        fun canEveryoneAccess(): Boolean = this == PUBLIC
        fun isEnabled(): Boolean = !isDisabled()
        fun isDisabled(): Boolean = this == DISABLED
        fun isUnknown(): Boolean = this == UNKNOWN
        fun canOnlyProjectMembersAccess(): Boolean = this == PRIVATE
    }
}
