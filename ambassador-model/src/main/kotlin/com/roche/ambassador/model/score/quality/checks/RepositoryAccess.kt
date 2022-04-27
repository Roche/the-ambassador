package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.project.Permissions

internal object RepositoryAccess : PermissionCheck() {
    override fun readPermission(permissions: Permissions): Permissions.Permission = permissions.repository

    override fun description(): String = "Repository"

    override fun targetDescription(): String = "repository"

    override fun name(): String = Check.REPOSITORY_ACCESS
}
