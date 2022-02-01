package com.roche.ambassador.storage.indexing

import com.roche.ambassador.Identifiable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "indexing")
data class Indexing internal constructor(
    @Id @GeneratedValue
    private var id: UUID? = null,
    @Column(name = "started_by")
    var startedBy: String = "unknown",
    @Column(name = "started_date")
    var startedDate: LocalDateTime = LocalDateTime.now(),
    @Column(name = "finished_date")
    var finishedDate: LocalDateTime? = null,
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: IndexingStatus = IndexingStatus.IN_PROGRESS,
    @Column(name = "target")
    var target: String = ALL_TARGET,
    @Embedded var stats: IndexingStatistics? = null,
    @Embedded
    @AttributeOverride(name = "id", column = Column(name = "indexing_lock"))
    var lock: Lock? = null,
    @Column(name = "source")
    var source: String,
) : Identifiable<UUID> {

    override fun getId(): UUID? = id

    override fun setId(id: UUID?) {
        this.id = id
    }

    fun finish(status: IndexingStatus, stats: IndexingStatistics? = null): Indexing {
        this.status = status
        this.finishedDate = LocalDateTime.now()
        this.stats = stats
        return this
    }

    fun fail(): Indexing = finish(IndexingStatus.FAILED)

    fun cancel(stats: IndexingStatistics? = null): Indexing = finish(IndexingStatus.CANCELLED, stats)

    fun isLocked(): Boolean = lock?.getId() != null

    fun lock(): Indexing {
        if (!isLocked()) {
            lock = Lock(UUID.randomUUID())
        }
        return this
    }

    fun unlock(): Indexing {
        if (isLocked()) {
            lock?.setId(null)
            lock = null
        }
        return this
    }

    fun isIndexingAll(): Boolean = target == ALL_TARGET

    companion object {
        const val ALL_TARGET = "__ALL__"

        fun start(startedBy: String = "unknown", source: String, target: String = ALL_TARGET): Indexing = Indexing(
            startedBy = startedBy,
            target = target,
            source = source
        )

        fun startAll(startedBy: String = "unknown", source: String): Indexing = start(startedBy, source)
    }
}
