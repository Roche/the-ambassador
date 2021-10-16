package com.filipowm.ambassador.storage.project

import com.filipowm.ambassador.model.project.Project
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project_history")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
class ProjectHistoryEntity(
    @Id @GeneratedValue var id: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false, updatable = false)
    var parent: ProjectEntity? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", updatable = false)
    @Basic(fetch = FetchType.LAZY)
    var project: Project? = null,
    @Column(name = "indexed_date", updatable = false)
    var indexedDate: LocalDateTime = LocalDateTime.now()
) {

    companion object Factory {
        fun from(projectEntity: ProjectEntity): ProjectHistoryEntity {
            return ProjectHistoryEntity(
                null, projectEntity,
                projectEntity.project, projectEntity.lastIndexedDate
            )
        }
    }
}
