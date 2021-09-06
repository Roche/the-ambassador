package com.filipowm.ambassador.project.indexer

import kotlinx.coroutines.sync.Semaphore

sealed class IndexingLock {
    abstract fun tryLock(): Boolean
    abstract fun isLocked(): Boolean
    abstract fun unlock()
}

class InMemoryIndexinglock : IndexingLock() {

    private val localLock = Semaphore(1)

    override fun tryLock() = localLock.tryAcquire()

    override fun unlock() = localLock.release()

    override fun isLocked() = localLock.availablePermits == 0
}

