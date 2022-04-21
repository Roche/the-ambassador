package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.model.project.Permissions

internal object PullRequestsAccess : PermissionCheck() {
    override fun readPermission(permissions: Permissions): Permissions.Permission = permissions.pullRequests

    override fun description(): String = "Pull Requests access"

    override fun targetDescription(): String = "pull requests"

    override fun name(): String = Check.PULL_REQUESTS_ACCESS
}
