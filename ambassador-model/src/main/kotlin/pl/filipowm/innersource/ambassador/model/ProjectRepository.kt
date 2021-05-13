package pl.filipowm.innersource.ambassador.model

import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectRepository {

    fun getById(id: String): Optional<Project>
    fun getByPath(path: String): Optional<Project>
    fun flow(filter: ProjectFilter?): Flow<Project>
    fun save(project: Project)

}