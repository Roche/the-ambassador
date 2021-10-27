package com.roche.ambassador.storage.indexing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class IndexingTest {

    @Test
    fun `new indexing should be already in progress`() {
        val indexing = Indexing()
        assertThat(indexing.status).isEqualTo(IndexingStatus.IN_PROGRESS)
        assertThat(indexing.lock).isNull()
        assertThat(indexing.startedDate)
            .isAfter(LocalDateTime.now().minusSeconds(5))
    }

    @Test
    fun `should update status and finish date when finishing indexing`() {
        // given
        val indexing = Indexing()
        val stats = IndexingStatistics(10, 10, 10, 10)

        // when
        indexing.finish(stats)

        // then
        assertThat(indexing)
            .extracting(Indexing::status, Indexing::stats)
            .containsExactly(IndexingStatus.FINISHED, stats)
        assertThat(indexing.finishedDate)
            .isAfter(indexing.startedDate)
    }

    @Test
    fun `should update status and finish date when failed indexing`() {
        // given
        val indexing = Indexing()

        // when
        indexing.fail()

        // then
        assertThat(indexing.status).isEqualTo(IndexingStatus.FAILED)
        assertThat(indexing.finishedDate)
            .isAfter(indexing.startedDate)
    }

    @Test
    fun `should create a lock if attempt to lock and not locked`() {
        // given
        val indexing = Indexing()

        // when
        indexing.lock()

        // then
        assertThat(indexing.lock)
            .isNotNull()
            .matches { it?.getId() != null }
    }

    @Test
    fun `should not create a lock if attempt to lock and already locked`() {
        // given
        val currentLock = Lock(UUID.randomUUID())
        val indexing = Indexing(lock = currentLock)

        // when
        indexing.lock()

        // then
        assertThat(indexing.lock)
            .isEqualTo(currentLock)
    }

    @Test
    fun `should release a lock if attempt to unlock and already locked`() {
        // given
        val currentLock = Lock(UUID.randomUUID())
        val indexing = Indexing(lock = currentLock)

        // when
        indexing.unlock()

        // then
        assertThat(indexing.lock).isNull()
    }

    @Test
    fun `indexing is locked when lock has ID`() {
        val lockedIndexing = Indexing(lock = Lock(UUID.randomUUID()))

        assertThat(lockedIndexing.isLocked()).isTrue()

        val unlockedIndexing = Indexing(lock = Lock())

        assertThat(unlockedIndexing.isLocked()).isFalse()

    }

}