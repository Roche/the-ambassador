package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.appendPoint
import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.feature.ProtectedBranchesFeature
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.project.ProtectedBranch
import com.roche.ambassador.model.score.quality.PartialCheckResult

internal object BranchProtectionCheck : Check {

    private val EXPECTED_RELEASE_BRANCH = "release/"
    private val DEVELOP_BRANCH_NAME = "development"

    override fun name(): String = Check.BRANCH_PROTECTION

    override fun check(project: Project): PartialCheckResult {
        return project.features.find(ProtectedBranchesFeature::class)
            .filter { it.value().exists() }
            .map { it.value().get() }
            .filter { it.isNotEmpty() }
            .map { check(it, project.defaultBranch) }
            .orElseGet { PartialCheckResult.noResult(name(), "") }
    }

    private fun check(branches: List<ProtectedBranch>, defaultBranch: String?): PartialCheckResult {
        val branchesToVerify = mutableMapOf("release" to EXPECTED_RELEASE_BRANCH)
        if (defaultBranch != null) {
            branchesToVerify[DEVELOP_BRANCH_NAME] = defaultBranch
        }
        val expected = branches.getExpected(branchesToVerify)
        val missing = branchesToVerify.keys - expected.map { it.key }.toSet()
        val reasonBuilder = StringBuilder()
        missing.forEach {
            reasonBuilder.appendLine("$it branch is missing branch protection")
        }
        val rated = expected
            .map { it.key to rateBranch(it.value, reasonBuilder) }
            .map { it.first to it.second.second }
        val reason = rated.map { it.first }.joinToString("\n")
        val total = rated.sumOf { it.second }
        val finalValue = total.toDouble() / branchesToVerify.size
        return PartialCheckResult.certain(name(), finalValue.round(0), reason)
    }

    private fun rateBranch(protections: List<ProtectedBranch>, reason: StringBuilder): Pair<String, Int> {
        val first = protections[0]
        reason.appendLine("Branch: " + first.name)
        if (first.canForcePush) {
            reason.appendPoint("force push is enabled, thus history rewrite is possible")
        }
        if (!first.canSomeoneMerge) {
            reason.appendPoint("no user can merge code into this branch")
        }
        if (first.canDeveloperPush) {
            reason.appendPoint("developers should not be allowed to push without merge request")
        }
        if (!first.canDeveloperMerge) {
            reason.appendPoint("developers should be allowed to merge code into this branch")
        }
        if (!first.codeReviewRequired) {
            reason.appendPoint("code review should be required")
        }
        if (first.canAdminPush) {
            reason.appendPoint("admins should not be allowed to push without code review")
        }

        val tier1 = !first.canForcePush && first.canSomeoneMerge
        val tier2 = tier1 && first.canDeveloperMerge && !first.canDeveloperPush
        val tier3 = tier2 && first.codeReviewRequired
        val tier4 = tier3 && !first.canAdminPush
        return Pair(reason.toString(), when {
            tier4 -> 10
            tier3 -> 9
            tier2 -> 6
            tier1 -> 3
            else -> 0
        })
    }

    private fun List<ProtectedBranch>.getExpected(expectedBranches: Map<String, String>): Map<String, List<ProtectedBranch>> {
        val map = mutableMapOf<String, MutableList<ProtectedBranch>>()
        for (protectedBranch in this) {
            val matchedBranchType = expectedBranches
                .filterValues { expectedBranch -> protectedBranch.name.contains(expectedBranch) }
                .map { it.key }
                .firstOrNull()
            if (matchedBranchType != null) {
                map
                    .computeIfAbsent(matchedBranchType) { mutableListOf() }
                    .add(protectedBranch)
            }
        }
        return map
    }
}