package com.filipowm.gitlab.api.project.model

enum class FeatureAccessLevel {

    DISABLED,
    PRIVATE,
    ENABLED
    ;

    fun canEveryoneAccess() = this == ENABLED
    fun canNooneAccess() = this == DISABLED
    fun canOnlyProjectMembersAccess() = this == PRIVATE

}