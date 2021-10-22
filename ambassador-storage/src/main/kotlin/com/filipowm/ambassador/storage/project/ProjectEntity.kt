package com.filipowm.ambassador.storage.project

import com.filipowm.ambassador.model.project.Project
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "project")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
class ProjectEntity(
    @Id var id: Long? = null,
    var name: String? = null,
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
    var lastIndexedDate: LocalDateTime = LocalDateTime.now(),
    @OneToMany(
        mappedBy = "parent",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @BatchSize(size = 25)
    @OrderBy("indexedDate")
    var history: MutableList<ProjectHistoryEntity> = mutableListOf()
) {

    fun wasIndexedBefore(otherDate: LocalDateTime): Boolean = lastIndexedDate.isBefore(otherDate)

    fun snapshot(): ProjectHistoryEntity {
        val historyEntry = ProjectHistoryEntity.from(this)
        history.add(historyEntry)
        return historyEntry
    }

    fun removeHistoryToMatchLimit(limit: Int) {
        val newHistory = history
            .sortedByDescending { it.indexedDate }
            .take(limit)
            .toMutableList()
        history.clear()
        history.addAll(newHistory)
    }

    fun updateIndex(project: Project) {
        this.name = project.name
        this.project = project
        this.stars = project.stats.stars
        this.criticalityScore = project.getScores().criticality
        this.activityScore = project.getScores().activity
        this.score = project.getScores().total
        this.lastIndexedDate = LocalDateTime.now()
    }

    companion object Factory {
        fun from(project: Project): ProjectEntity {
            return ProjectEntity(
                project.id, project.name,
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
