package com.roche.ambassador.project.indexer.internals

import com.roche.ambassador.configuration.properties.IndexingCriteriaProperties
import com.roche.ambassador.extensions.toHumanReadable
import com.roche.ambassador.model.project.Visibility
import com.roche.ambassador.model.source.IndexingCriteriaProvider
import com.roche.ambassador.model.source.ProjectDetailsResolver
import com.roche.ambassador.project.indexer.IndexingCriterion
import java.time.LocalDateTime

internal open class IndexingCriteria<T>(vararg criteria: IndexingCriterion<T>) {

    private val criteria = criteria.toList()

    companion object {

        fun forProvider(
            projectDetailsResolver: ProjectDetailsResolver<Any>,
            criteriaProvider: IndexingCriteriaProvider<Any>,
            props: IndexingCriteriaProperties
        ): IndexingCriteria<Any> {
            val builder =  CriteriaBuilder(projectDetailsResolver)
                .addIf("excludeAllForks", criteriaProvider.getForkedProjectCriteria().excludeAllWithForks(), props.forks.excludeAll)
                .addIf(
                    "personalProjectHasAtLeast${props.personalProjects.mustHaveAtLeastStars}Stars",
                    criteriaProvider.getPersonalProjectCriteria().hasAtLeastStars(props.personalProjects.mustHaveAtLeastStars)
                ) { props.personalProjects.mustHaveAtLeastStars > 0 }
                .addIf("hasDefaultBranch", criteriaProvider.getInvalidProjectCriteria().hasDefaultBranch(), props.projects.mustHaveDefaultBranch)
                .addIf("isRepositoryNotEmpty", criteriaProvider.getInvalidProjectCriteria().isRepositoryNotEmpty(), props.projects.mustHaveNotEmptyRepo)
                .addIf("canCreateMergeRequest", criteriaProvider.getInvalidProjectCriteria().canCreateMergeRequest(), props.projects.mustBeAbleToCreateMergeRequest)
                .addIf("canForkProject", criteriaProvider.getInvalidProjectCriteria().canForkProject(), props.projects.mustBeAbleToFork)
                .addIf("hasVisibilityAtMost${props.projects.maxVisibility}", criteriaProvider.getInvalidProjectCriteria().hasVisibilityAtMost(props.projects.maxVisibility), props.projects.maxVisibility != Visibility.PRIVATE)
                .addIf("isNotArchived", criteriaProvider.getInvalidProjectCriteria().isNotArchived(), !props.projects.includeArchived)
            val lastActivityWithin = props.projects.lastActivityWithin
            if (lastActivityWithin != null) {
                builder.add("lastActivityWithin${lastActivityWithin.toHumanReadable()}", criteriaProvider.getInvalidProjectCriteria().hasActivityAfter(LocalDateTime.now().minus(lastActivityWithin).toLocalDate()))
            }
            return builder.build()
        }
    }

    open fun evaluate(input: T): CriteriaEvaluationResult<T> {
        val failedCriteria = mutableListOf<IndexingCriterion<T>>()
        for (criterion in criteria) {
            if (!criterion.test(input)) {
                failedCriteria.add(criterion)
            }
        }
        return CriteriaEvaluationResult(failedCriteria)
    }

    fun getAllCriteriaNames() = criteria.joinToString(",") { it.name }

    data class CriteriaEvaluationResult<T> internal constructor(
        val failedCriteria: List<IndexingCriterion<T>> = listOf()
    ) {
        val success = failedCriteria.isEmpty()
        val failure = failedCriteria.isNotEmpty()

        fun whenFailed(block: (List<IndexingCriterion<T>>) -> Unit) {
            if (!success) {
                block.invoke(failedCriteria)
            }
        }
    }
}
