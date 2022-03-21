package com.roche.ambassador.storage.project

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project_statistics_history")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
class ProjectStatisticsHistory(
    @Id @GeneratedValue var id: UUID? = null,
    @Column(name = "project_id", nullable = false, updatable = false)
    var projectId: Long,
    @Type(type = "jsonb")
    @Column(name = "stats", columnDefinition = "jsonb", updatable = false)
    @Basic(fetch = FetchType.EAGER)
    var stats: ProjectStatistics,
    @Column(name = "record_date", updatable = false)
    var date: LocalDateTime = LocalDateTime.now()
) {

    companion object Factory {
        fun from(projectEntity: ProjectEntity): ProjectStatisticsHistory {
            val stats = ProjectStatistics.from(projectEntity.project)
            return ProjectStatisticsHistory(
                null, projectEntity.id!!, stats, projectEntity.lastIndexedDate
            )
        }
    }

}