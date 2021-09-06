package com.filipowm.ambassador.model.source

import com.filipowm.ambassador.model.Project
import com.filipowm.ambassador.model.ProjectFilter
import com.filipowm.ambassador.model.Specification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectSource<T>: Specification, IndexingCriteriaProvider<T>, ProjectDetailsResolver<T> {

    suspend fun getById(id: String): Optional<Project>
    suspend fun flow(filter: ProjectFilter): Flow<T>
    suspend fun map(input: T): Project

}