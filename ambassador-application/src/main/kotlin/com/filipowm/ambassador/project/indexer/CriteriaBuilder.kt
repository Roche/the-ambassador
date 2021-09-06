package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.model.source.CriterionVerifier
import com.filipowm.ambassador.model.source.ProjectDetailsResolver
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

internal class CriteriaBuilder<T>(private val projectDetailsResolver: ProjectDetailsResolver<T>) {
    private val criteria: MutableSet<IndexingCriterion<T>> = mutableSetOf()

    fun addCriteria(name: String, verifier: CriterionVerifier<T>): CriteriaBuilder<T> {
        criteria.add(IndexingCriterion(name, verifier, projectDetailsResolver))
        return this
    }

    fun createCriteriaFrom(criteriaHolder: Any): CriteriaBuilder<T> {
        val createdCriteria = criteriaHolder.javaClass.declaredMethods
            .filter {
                val returnType = it.kotlinFunction?.returnType
                if (returnType != null) {
                    val isFunction = returnType.jvmErasure.isSubclassOf(kotlin.jvm.functions.Function1::class)
                    val hasBooleanReturnType = returnType.arguments.size == 2 && returnType.arguments[1].type?.isSubtypeOf(Boolean::class.createType()) ?: false
                    isFunction && hasBooleanReturnType
                } else {
                    false
                }
            }
            .map { it.name to it.invoke(criteriaHolder) as CriterionVerifier<T> }
            .map { IndexingCriterion(it.first, it.second, projectDetailsResolver) }
            .toList()
        this.criteria.addAll(createdCriteria)
        return this
    }

    fun build(): IndexingCriteria<T> {
        return IndexingCriteria(*criteria.toTypedArray())
    }
}