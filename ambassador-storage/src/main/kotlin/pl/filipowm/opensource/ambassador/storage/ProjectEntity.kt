package pl.filipowm.opensource.ambassador.storage

import org.hibernate.annotations.Type
import pl.filipowm.opensource.ambassador.model.Project
import javax.persistence.*

@Entity
class ProjectEntity(
    @Id var id: Long? = null,
    var name: String? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    var project: Project? = null,
    var stars: Int = 0,
    var criticalityScore: Double? = 0.0,
    var activityScore: Double? = 0.0
) {
    companion object Factory {
        fun from(project: Project) : ProjectEntity {
            return ProjectEntity(
                project.id, project.name, project,
                project.stats.stars,
                project.getScores().criticality,
                project.getScores().activity
            )
        }
    }
}