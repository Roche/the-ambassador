package pl.filipowm.opensource.ambassador.model

import kotlinx.coroutines.flow.Flow
import java.util.*

interface SourceProjectRepository<T> {
    suspend fun flow(filter: ProjectFilter): Flow<T>
    suspend fun getById(id: String): Optional<Project>
    suspend fun getByPath(path: String): Optional<Project>
    fun mapper(): ProjectMapper<T>
}