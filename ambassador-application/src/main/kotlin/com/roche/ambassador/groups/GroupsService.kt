package com.roche.ambassador.groups

import com.roche.ambassador.commons.api.Paged
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.projects.SimpleProjectDto
import com.roche.ambassador.storage.group.GroupEntityRepository
import com.roche.ambassador.storage.group.GroupSearchQuery
import com.roche.ambassador.storage.group.GroupSearchRepository
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
internal class GroupsService(
    private val projectEntityRepository: ProjectEntityRepository,
    private val groupEntityRepository: GroupEntityRepository,
    private val groupSearchRepository: GroupSearchRepository,
) {

    companion object {
        private val log by LoggerDelegate()
    }

    fun getGroup(id: Long): GroupDto {
        log.debug("Retrieving group $id")
        // TODO add tables relationships to be able to efficiently join this data
        return groupEntityRepository.findById(id)
            .map { it.group to projectEntityRepository.findAllByParentId(id) }
            .map { it.first to it.second.map { entity -> entity.project } }
            .map { it.first to it.second.map { project -> SimpleProjectDto.from(project) } }
            .map { GroupDto(it.first, it.second) }
            .orElseThrow { Exceptions.NotFoundException("Project $id not found") }
    }

    fun search(query: ListGroupsQuery, pageable: Pageable): Paged<SimpleGroupDto> {
        log.debug("Searching for group with query: {}", query)
        val q = GroupSearchQuery(query.query, query.visibility.orElse(Visibility.INTERNAL))
        val result = groupSearchRepository.search(q, pageable)
            .map { SimpleGroupDto.from(it.group) }
        return Paged.from(result)
    }
}