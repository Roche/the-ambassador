package pl.filipowm.opensource.ambassador.project.indexer

import java.util.concurrent.Semaphore
import kotlin.math.max

internal class TimelyBlockingSemaphore(private val permits: Int = 1, private val allowAcquireAfterMillis: Long = 0) : Semaphore(permits) {

    companion object {
        private val MUTEX = Object()
    }

    @Volatile
    private var lastTouch = 0L

    fun touch() {
        lastTouch = System.currentTimeMillis()
    }

    fun unlocksIn(): Long {
        return max(0, allowAcquireAfterMillis + lastTouch - System.currentTimeMillis())
    }

    override fun tryAcquire(): Boolean {
        synchronized(MUTEX) {
            tryRelease()
            val acquired = super.tryAcquire()
            if (acquired) {
                touch()
            }
            return acquired
        }
    }

    private fun tryRelease(): Boolean {
        compensatePermits()
        if (allowRelease()) {
            super.release()
            return true
        }
        return false
    }

    private fun allowRelease(): Boolean {
        return System.currentTimeMillis() - lastTouch > allowAcquireAfterMillis && this.availablePermits() < this.permits
    }

    private fun compensatePermits() {
        if (this.availablePermits() > this.permits) {
            this.reducePermits(this.availablePermits() - this.permits)
        }
    }
}
