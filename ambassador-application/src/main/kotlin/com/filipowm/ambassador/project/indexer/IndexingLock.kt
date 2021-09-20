package com.filipowm.ambassador.project.indexer

import kotlinx.coroutines.sync.Semaphore

sealed class IndexingLock {
    abstract fun tryLock(): Boolean
    abstract fun isLocked(): Boolean
    abstract fun unlock()
}

class InMemoryIndexingLock : IndexingLock() {

    private val localLock = Semaphore(1)

    override fun tryLock(): Boolean = localLock.tryAcquire()

    override fun unlock(): Unit = localLock.release()

    override fun isLocked(): Boolean = localLock.availablePermits == 0
}
