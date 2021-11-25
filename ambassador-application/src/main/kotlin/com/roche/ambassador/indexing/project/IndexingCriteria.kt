package com.roche.ambassador.indexing.project

import com.roche.ambassador.configuration.properties.IndexingCriteriaProperties
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.source.IndexingCriteriaProvider
import com.roche.ambassador.indexing.IndexingCriterion
import java.time.LocalDateTime

class IndexingCriteria(vararg criteria: IndexingCriterion) {

    private val criteria = criteria.toList()

    companion object {

        fun forProvider(
            criteriaProvider: IndexingCriteriaProvider,
            props: IndexingCriteriaProperties
        ): IndexingCriteria {
            val builder = CriteriaBuilder()
                .addIf("excludeAllForks", criteriaProvider.getInvalidProjectCriteria().excludeForked(), props.projects.excludeForks)
                .addIf(
                    "personalProjectHasAtLeast${props.personalProjects.mustHaveAtLeastStars}Stars",
                    criteriaProvider.getPersonalProjectCriteria().hasAtLeastStars(props.personalProjects.mustHaveAtLeastStars)
                ) { props.personalProjects.mustHaveAtLeastStars > 0 }
                .addIf("hasDefaultBranch", criteriaProvider.getInvalidProjectCriteria().hasDefaultBranch(), props.projects.mustHaveDefaultBranch)
                .addIf("isRepositoryNotEmpty", criteriaProvider.getInvalidProjectCriteria().isRepositoryNotEmpty(), props.projects.mustHaveNotEmptyRepo)
                .addIf("canCreateMergeRequest", criteriaProvider.getInvalidProjectCriteria().canCreateMergeRequest(), props.projects.mustBeAbleToCreateMergeRequest)
                .addIf("canForkProject", criteriaProvider.getInvalidProjectCriteria().canForkProject(), props.projects.mustBeAbleToFork)
                .addIf(
                    "hasVisibilityAtMost${props.projects.maxVisibility}", criteriaProvider.getInvalidProjectCriteria().hasVisibilityAtMost(props.projects.maxVisibility),
                    props.projects.maxVisibility != Visibility.PRIVATE
                )
                .addIf("excludeArchived", criteriaProvider.getInvalidProjectCriteria().excludeArchived(), props.projects.excludeArchived)
            val lastActivityWithin = props.projects.lastActivityWithin
            if (lastActivityWithin != null) {
                builder.add(
                    "lastActivityWithin${lastActivityWithin.toHumanReadable()}",
                    criteriaProvider.getInvalidProjectCriteria().hasActivityAfter(LocalDateTime.now().minus(lastActivityWithin).toLocalDate())
                )
            }
            return builder.build()
        }
    }

    open fun evaluate(input: Project): CriteriaEvaluationResult<Project> {
        val failedCriteria = mutableListOf<IndexingCriterion>()
        for (criterion in criteria) {
            if (!criterion.test(input)) {
                failedCriteria.add(criterion)
            }
        }
        return CriteriaEvaluationResult(failedCriteria)
    }

    fun getAllCriteriaNames() = criteria.joinToString(",") { it.name }

    data class CriteriaEvaluationResult<T> internal constructor(
        val failedCriteria: List<IndexingCriterion> = listOf()
    ) {
        val success = failedCriteria.isEmpty()
        val failure = failedCriteria.isNotEmpty()

        fun whenFailed(block: (List<IndexingCriterion>) -> Unit) {
            if (!success) {
                block.invoke(failedCriteria)
            }
        }
    }
}
