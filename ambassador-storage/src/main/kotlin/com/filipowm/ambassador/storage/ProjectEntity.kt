package com.filipowm.ambassador.storage

import com.filipowm.ambassador.model.project.Project
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
class ProjectEntity(
    @Id var id: Long? = null,
    var name: String? = null,
    var excerpt: String? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    var project: Project? = null,
    var stars: Int = 0,
    @Column(name = "criticality_score")
    var criticalityScore: Double? = 0.0,
    @Column(name = "activity_score")
    var activityScore: Double? = 0.0,
    @Column(name = "score")
    var score: Double? = 0.0,
    @Column(name = "last_indexed_date")
    var lastIndexedDate: LocalDateTime = LocalDateTime.now()
) {

    fun wasIndexedBefore(otherDate: LocalDateTime): Boolean = lastIndexedDate.isBefore(otherDate)

    companion object Factory {
        val EXCERPT_LENGTH = 1024

        fun from(project: Project): ProjectEntity {
            val excerpt = Optional.ofNullable(project.description)
                .map { it.substring(0, minOf(it.length, EXCERPT_LENGTH)) }
                .orElse(null)
            return ProjectEntity(
                project.id, project.name,
                excerpt,
                project,
                project.stats.stars,
                project.getScores().criticality,
                project.getScores().activity,
                project.getScores().total,
                LocalDateTime.now()
            )
        }
    }
}
