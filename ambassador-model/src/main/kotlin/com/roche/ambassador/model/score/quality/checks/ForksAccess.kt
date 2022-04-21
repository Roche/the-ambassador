package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.project.Permissions

internal object ForksAccess : PermissionCheck() {
    override fun readPermission(permissions: Permissions): Permissions.Permission = permissions.forks

    override fun description(): String = "Forks access"

    override fun targetDescription(): String = "forks"

    override fun name(): String = Check.FORKS_ACCESS
}
