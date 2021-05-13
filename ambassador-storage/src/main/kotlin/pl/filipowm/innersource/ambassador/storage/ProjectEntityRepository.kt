package pl.filipowm.innersource.ambassador.storage

import org.springframework.data.repository.CrudRepository
import java.util.*

interface ProjectEntityRepository : CrudRepository<ProjectEntity, Long> {

    override fun findById(id: Long) : Optional<ProjectEntity>

}