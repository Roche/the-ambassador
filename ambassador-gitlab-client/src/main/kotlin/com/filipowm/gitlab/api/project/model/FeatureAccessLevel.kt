package com.filipowm.gitlab.api.project.model

enum class FeatureAccessLevel {

    DISABLED,
    PRIVATE,
    ENABLED
    ;

    fun canEveryoneAccess(): Boolean = this == ENABLED
    fun canNooneAccess(): Boolean = this == DISABLED
    fun canOnlyProjectMembersAccess(): Boolean = this == PRIVATE

}