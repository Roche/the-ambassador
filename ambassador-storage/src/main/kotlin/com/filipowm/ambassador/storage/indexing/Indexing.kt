package com.filipowm.ambassador.storage.indexing

import com.filipowm.ambassador.storage.Identifiable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "indexing")
class Indexing internal constructor(
    @Id @GeneratedValue
    private var id: UUID? = null,
    @Column(name = "started_by")
    var startedBy: String? = null,
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
    var lock: Lock? = null
) : Identifiable {

    override fun getId(): UUID? = id

    fun finish(stats: IndexingStatistics? = null): Indexing {
        this.status = IndexingStatus.FINISHED
        this.finishedDate = LocalDateTime.now()
        this.stats = stats
        return this
    }

    fun fail(): Indexing {
        this.status = IndexingStatus.FAILED
        this.finishedDate = LocalDateTime.now()
        return this
    }

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
        private const val ALL_TARGET = "__ALL__"

        fun start(startedBy: String = "unknown", target: String): Indexing = Indexing(
            startedBy = startedBy,
            target = target
        )

        fun startAll(startedBy: String = "unknown"): Indexing = start(startedBy, ALL_TARGET)
    }

}