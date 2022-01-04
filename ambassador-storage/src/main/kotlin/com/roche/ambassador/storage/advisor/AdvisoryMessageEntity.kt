package com.roche.ambassador.storage.advisor

import com.roche.ambassador.Identifiable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "advisory_message")
class AdvisoryMessageEntity(
    @Id @GeneratedValue
    private var id: UUID? = null,
    @Column(name = "name")
    private var name: String,
    @Column(name = "created_date")
    var createdDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_date")
    var updatedDate: LocalDateTime? = null,
    @Column(name = "closed_date")
    var closedDate: LocalDateTime? = null,
    @Column(name = "project_id")
    var projectId: Long, // do not reference project table here, to not build unnecessary (really!) coupling
    @Column(name = "source")
    var source: String,
    @Column(name = "reference_id")
    var referenceId: Long,
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    var type: Type,
) : Identifiable<UUID> {

    enum class Type {
        ISSUE,
        PULL_REQUEST, // TODO not yet supported
    }

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }
}
