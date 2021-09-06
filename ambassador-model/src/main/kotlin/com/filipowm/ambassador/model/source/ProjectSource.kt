package com.filipowm.ambassador.model.source

import com.filipowm.ambassador.model.Specification
import com.filipowm.ambassador.model.project.Project
import com.filipowm.ambassador.model.project.ProjectFilter
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectSource<T>: Specification, IndexingCriteriaProvider<T>, ProjectDetailsResolver<T> {

    suspend fun getById(id: String): Optional<Project>
    suspend fun flow(filter: ProjectFilter): Flow<T>
    suspend fun map(input: T): Project

}