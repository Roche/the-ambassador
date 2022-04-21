package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.project.Permissions

internal object IssuesAccess : PermissionCheck() {

    override fun name(): String = Check.ISSUES_ACCESS

    override fun readPermission(permissions: Permissions): Permissions.Permission = permissions.issues

    override fun description(): String = "Issues access"

    override fun targetDescription(): String = "issues"

}
