package com.roche.gitlab.api.project.model

enum class FeatureAccessLevel {

    PUBLIC,
    DISABLED,
    PRIVATE,
    ENABLED
    ;

    fun canEveryoneAccess(): Boolean = this == ENABLED || this == PUBLIC
    fun canNooneAccess(): Boolean = this == DISABLED
    fun canOnlyProjectMembersAccess(): Boolean = this == PRIVATE
}
