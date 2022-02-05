package com.roche.ambassador.storage.project

import com.roche.ambassador.model.project.Project
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "project")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
@NamedEntityGraph(
    name = "Project.history",
    attributeNodes = [NamedAttributeNode("history")]
)
class ProjectEntity(
    @Id var id: Long? = null,
    var name: String? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    var project: Project,
    var language: String? = null,
    var stars: Int = 0,
    @Column(name = "criticality_score")
    var criticalityScore: Double? = 0.0,
    @Column(name = "activity_score")
    var activityScore: Double? = 0.0,
    @Column(name = "score")
    var score: Double? = 0.0,
    @Column(name = "last_indexed_date")
    var lastIndexedDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "last_analysis_date")
    var lastAnalysisDate: LocalDateTime? = null,
    @OneToMany(
        mappedBy = "parent",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @BatchSize(size = 25)
    @OrderBy("indexedDate")
    var history: MutableList<ProjectHistoryEntity> = mutableListOf(),
    var source: String? = null,
    @Column(name = "last_indexing_id")
    var lastIndexingId: UUID? = null // mapping is not needed here yet, thus not adding it

) {

    fun wasIndexedBefore(otherDate: LocalDateTime): Boolean = lastIndexedDate.isBefore(otherDate)

    fun wasIndexedBefore(otherDate: LocalDate): Boolean = lastIndexedDate.isBefore(otherDate.atStartOfDay())

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
        this.stars = project.stats.stars ?: 0
        this.lastIndexedDate = LocalDateTime.now()
        this.language = project.getMainLanguage()
    }

    fun updateScore(project: Project) {
        this.criticalityScore = project.getScores().criticality
        this.activityScore = project.getScores().activity
        this.score = project.getScores().total
        this.lastAnalysisDate = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectEntity

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    companion object Factory {
        fun from(project: Project): ProjectEntity {
            val lang = project.getMainLanguage()
            return ProjectEntity(
                project.id, project.name,
                project, lang,
                project.stats.stars ?: 0,
                project.getScores().criticality,
                project.getScores().activity,
                project.getScores().total,
                LocalDateTime.now(),
            )
        }
    }
}
