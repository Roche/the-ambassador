package pl.filipowm.opensource.ambassador.storage

import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface ProjectEntityRepository : PagingAndSortingRepository<ProjectEntity, Long> {

    override fun findById(id: Long): Optional<ProjectEntity>
}
