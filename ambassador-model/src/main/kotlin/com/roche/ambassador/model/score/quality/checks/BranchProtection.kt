package com.roche.ambassador.model.score.quality.checks

import com.roche.ambassador.extensions.round
import com.roche.ambassador.model.Explanation
import com.roche.ambassador.model.feature.DefaultBranchFeature
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.feature.ProtectedBranchesFeature
import com.roche.ambassador.model.project.ProtectedBranch
import com.roche.ambassador.model.score.quality.PartialCheckResult

internal object BranchProtection : Check {

    private const val EXPECTED_RELEASE_BRANCH = "release/"
    private const val DEVELOP_BRANCH_NAME = "development"

    override fun name(): String = Check.BRANCH_PROTECTION

    override fun check(features: Features): PartialCheckResult {
        val defaultBranchOptional = features.findValue(DefaultBranchFeature::class)
        if (defaultBranchOptional.isEmpty) {
            return PartialCheckResult.empty(name())
        }
        val defaultBranch = defaultBranchOptional.get()
        return features.findValue(ProtectedBranchesFeature::class)
            .filter { it.isNotEmpty() }
            .map { check(it, defaultBranch) }
            .orElseGet { PartialCheckResult.empty(name()) }
    }

    private fun check(branches: List<ProtectedBranch>, defaultBranch: String): PartialCheckResult {
        val branchesToVerify = mapOf(
            "release" to EXPECTED_RELEASE_BRANCH,
            DEVELOP_BRANCH_NAME to defaultBranch
        )
        val expected = branches.getExpected(branchesToVerify)
        val missing = branchesToVerify.keys - expected.map { it.key }.toSet()
        val explanationBuilder = Explanation.Builder()
            .description("Branch protection configuration")
        missing.forEach {
            explanationBuilder.addDetails("$it branch is missing branch protection")
        }
        val rated = expected
            .map { rateBranch(it.value, explanationBuilder) }
        val total = rated.sumOf { it }
        val finalValue = total.toDouble() / branchesToVerify.size
        return PartialCheckResult.builder(name())
            .certain()
            .score(finalValue.round(0))
            .explanation(explanationBuilder)
            .build()
    }

    private fun rateBranch(protections: List<ProtectedBranch>, explanationBuilder: Explanation.Builder): Int {
        val first = protections[0]
        val tier1 = !first.canForcePush && first.canSomeoneMerge
        val tier2 = tier1 && first.canDeveloperMerge && !first.canDeveloperPush
        val tier3 = tier2 && first.codeReviewRequired
        val tier4 = tier3 && !first.canAdminPush
        val score = when {
            tier4 -> 10
            tier3 -> 9
            tier2 -> 6
            tier1 -> 3
            else -> 0
        }
        if (!tier4) {
            explanationBuilder.addChild {
                it.maxValue(10.0.round(0))
                    .value(score.toDouble().round(0))
                    .description("Branch ${first.name} is misconfigured")
                if (first.canForcePush) {
                    it.addDetails("force push is allowed, thus history rewrite is possible")
                }
                if (!first.canSomeoneMerge) {
                    it.addDetails("no user can merge code into this branch")
                }
                if (first.canDeveloperPush) {
                    it.addDetails("developers should not be allowed to push without merge request")
                }
                if (!first.canDeveloperMerge) {
                    it.addDetails("developers should be allowed to merge code into this branch")
                }
                if (!first.codeReviewRequired) {
                    it.addDetails("code review should be required")
                }
                if (first.canAdminPush) {
                    it.addDetails("admins should not be allowed to push without code review")
                }
            }
        }
        return score
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