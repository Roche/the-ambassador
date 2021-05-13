package pl.filipowm.innersource.ambassador.storage

import org.hibernate.annotations.Type
import pl.filipowm.innersource.ambassador.model.Project
import javax.persistence.*

@Entity
class ProjectEntity(
    @Id var id: Long? = null,
    var name: String? = null,
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    var project: Project? = null
) {
    companion object Factory {
        fun from(project: Project) : ProjectEntity {
            return ProjectEntity(project.id, project.name, project)
        }
    }
}