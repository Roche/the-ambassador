package com.roche.ambassador.advisor.api

import com.roche.ambassador.advisor.AdvisorManager
import com.roche.ambassador.advisor.messages.AdviceMessage
import com.roche.ambassador.exceptions.Exceptions
import com.roche.ambassador.storage.project.ProjectEntityRepository
import org.springframework.stereotype.Service

@Service
class AdvisorService(
    private val advisorManager: AdvisorManager,
    private val projectEntityRepository: ProjectEntityRepository
) {

    suspend fun readAdvices(projectId: Long): List<AdviceDto> {
        val project = projectEntityRepository.findById(projectId)
            .map { it.project }
            .orElseThrow { Exceptions.NotFoundException("Project $projectId does not exist") }
        return advisorManager
            .readAdvices(project)
            .map { fromMessage(it) }
    }

    private fun fromMessage(adviceMessage: AdviceMessage): AdviceDto {
        return AdviceDto(
            adviceMessage.name,
            adviceMessage.details,
            adviceMessage.reason,
            adviceMessage.remediation,
            adviceMessage.severity
        )
    }

}