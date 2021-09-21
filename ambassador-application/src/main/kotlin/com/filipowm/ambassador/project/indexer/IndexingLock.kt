package com.filipowm.ambassador.project.indexer

import com.filipowm.ambassador.storage.indexing.Indexing
import com.filipowm.ambassador.storage.indexing.IndexingRepository
import kotlinx.coroutines.sync.Semaphore
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

sealed class IndexingLock {
    abstract fun tryLock(indexing: Indexing): Boolean
    abstract fun isLocked(indexing: Indexing = Indexing.startAll()): Boolean
    abstract fun unlock(indexing: Indexing)
}

class InMemoryIndexingLock : IndexingLock() {

    private val localLock = Semaphore(1)

    override fun tryLock(indexing: Indexing): Boolean = localLock.tryAcquire()

    override fun unlock(indexing: Indexing): Unit = localLock.release()

    override fun isLocked(indexing: Indexing): Boolean = isLocked()

}

class DatabaseIndexingLock(private val indexingRepository: IndexingRepository) : IndexingLock() {

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
            false
        } catch (e: ConstraintViolationException) {
            false
        }
    }

    override fun unlock(indexing: Indexing) {
        indexingRepository.findByLockIsNotNullAndTarget(indexing.target)
            .ifPresent { indexingRepository.save(it.unlock()) }
    }

    override fun isLocked(indexing: Indexing): Boolean {
        return indexingRepository.findByLockIsNotNullAndTarget(indexing.target).isPresent
    }
}
