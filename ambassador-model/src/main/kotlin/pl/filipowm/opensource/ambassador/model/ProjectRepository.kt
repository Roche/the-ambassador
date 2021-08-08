package pl.filipowm.opensource.ambassador.model

import reactor.core.publisher.ParallelFlux
import java.util.*

interface ProjectRepository {

    fun getById(id: String): Optional<Project>
    fun getByPath(path: String): Optional<Project>
    fun list(filter: ProjectFilter): ParallelFlux<Project>
    fun save(project: Project)

}