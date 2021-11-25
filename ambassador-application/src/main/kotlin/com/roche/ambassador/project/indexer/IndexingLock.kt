package com.roche.ambassador.project.indexer

import com.roche.ambassador.extensions.LoggerDelegate
import com.roche.ambassador.storage.indexing.Indexing
import com.roche.ambassador.storage.indexing.IndexingRepository
import kotlinx.coroutines.sync.Semaphore
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import java.util.*

internal sealed class IndexingLock {
    abstract fun tryLock(indexing: Indexing): Boolean
    abstract fun isLocked(indexing: Indexing = Indexing.startAll(source = "_")): Boolean
    abstract fun isLocked(indexingId: UUID): Boolean
    abstract fun unlock(indexingId: UUID)

    companion object Factory {
        fun createInMemoryLock(): IndexingLock = InMemoryIndexingLock()
        fun createDatabaseLock(indexingRepository: IndexingRepository): IndexingLock = DatabaseIndexingLock(indexingRepository)
    }
}

private class InMemoryIndexingLock : IndexingLock() {

    private val localLock = Semaphore(1)

    override fun tryLock(indexing: Indexing): Boolean = localLock.tryAcquire()

    override fun unlock(indexingId: UUID) {
        if (!hasPermits()) {
            localLock.release()
        }
    }

    override fun isLocked(indexing: Indexing): Boolean = !hasPermits()

    override fun isLocked(indexingId: UUID): Boolean = !hasPermits()

    private fun hasPermits(): Boolean = localLock.availablePermits > 0
}

private class DatabaseIndexingLock(private val indexingRepository: IndexingRepository) : IndexingLock() {

    companion object {
        private val logger by LoggerDelegate()
    }

    override fun tryLock(indexing: Indexing): Boolean {
        return if (isLocked(indexing)) {
            false
        } else {
            attemptLock(indexing)
        }
    }

    private fun attemptLock(indexing: Indexing): Boolean {
        return try {
            indexingRepository.save(indexing.lock())
            true
        } catch (e: DataIntegrityViolationException) {
            logger.debug("Unable to lock due to data integrity violation", e)
            false
        } catch (e: ConstraintViolationException) {
            logger.debug("Unable to lock due to constraint violation", e)
            false
        }
    }

    override fun unlock(indexingId: UUID) {
        indexingRepository.findById(indexingId)
            .ifPresent { indexingRepository.save(it.unlock()) }
    }

    override fun isLocked(indexing: Indexing): Boolean {
        return indexingRepository.findByLockIsNotNullAndTarget(indexing.target).isPresent
    }

    override fun isLocked(indexingId: UUID): Boolean {
        return indexingRepository.findById(indexingId)
            .filter { it.isLocked() }
            .isEmpty
    }
}
