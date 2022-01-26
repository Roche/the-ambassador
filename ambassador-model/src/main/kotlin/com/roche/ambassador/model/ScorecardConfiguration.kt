package com.roche.ambassador.model

import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.score.quality.CheckMetadata
import com.roche.ambassador.model.score.quality.checks.Check
import com.roche.ambassador.model.score.quality.checks.ChecksRegistry

data class ScorecardConfiguration(
    val requireVisibility: List<Visibility>,
    val activity: ActivityPolicyConfiguration = ActivityPolicyConfiguration(),
    val criticality: CriticalityPolicyConfiguration = CriticalityPolicyConfiguration(),
    val quality: QualityPolicyConfiguration
) {

    data class QualityPolicyConfiguration(
        val enabled: Boolean = true,
        val experimental: Boolean = true,
        val checks: Map<String, CheckMetadata>
    ) {

        fun getEnabledChecksConfiguration(): Map<String, CheckMetadata> {
            return checks.filterValues { it.enabled }
        }

        fun getEnabledChecks(): List<Check> {
            return getEnabledChecksConfiguration()
                .map {
                    ChecksRegistry[it.key].orElseThrow {
                        IllegalStateException(
                            "Check ${it.key} does not exist in registry. Verify if name in both configuration and registry match or register missing check."
                        )
                    }
                }
        }
    }

    data class ActivityPolicyConfiguration(val enabled: Boolean = true)

    data class CriticalityPolicyConfiguration(val enabled: Boolean = true)

    fun shouldCalculateScoring(project: Project): Boolean {
        return project.visibility in requireVisibility
    }
}