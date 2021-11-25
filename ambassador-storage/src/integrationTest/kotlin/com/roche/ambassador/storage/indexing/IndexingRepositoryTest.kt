package com.roche.ambassador.storage.indexing

import com.roche.ambassador.storage.PersistenceTest
import com.roche.ambassador.storage.indexing.Indexing.Companion.ALL_TARGET
import com.roche.ambassador.storage.utils.*
import com.roche.ambassador.storage.utils.QueryAssertions.Companion.assertQueryCount
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

@PersistenceTest
internal class IndexingRepositoryTest(@Autowired private val indexingRepository: IndexingRepository) {

    @AfterEach
    fun cleanupRepositoryAfter() {
        indexingRepository.deleteAll()
    }

    @Nested
    @DisplayName("basic operations indexing repository test")
    inner class BasicIndexingOperationsTest {

        @Test
        fun `should create single indexing`() {
            // given
            val indexing = Indexing.start(source = "whatever", target = ALL_TARGET)

            // when
            val saved = indexingRepository.save(indexing)

            // then
            assertThat(indexingRepository.findAll()).hasSize(1)
            assertThat(saved.getId()).isNotNull()
            assertQueryCount().hasInserted(1).hasSelected(1)
        }

        @Test
        fun `should be able to store indexing history`() {
            // when
            indexingRepository.saveAll(
                listOf(Indexing.startAll(source = "whatever", ).finish(), Indexing.startAll(source = "whatever", ).finish(), Indexing.startAll(source = "whatever", ).finish(), Indexing.startAll(source = "whatever", ).finish())
            )

            // then
            assertThat(indexingRepository.findAll()).hasSize(4)
            assertQueryCount().hasInserted(4)
        }

        @Test
        fun `should throw exception when saving two entries with a lock and same target`() {
            // given
            val indexing = Indexing.start(source = "whatever", target = ALL_TARGET)
            indexing.lock()
            val indexing2 = Indexing.start(source = "whatever", target = ALL_TARGET)
            indexing2.lock()

            // when & then
            indexingRepository.save(indexing)
            assertThatThrownBy { indexingRepository.save(indexing2) }
                .isInstanceOf(DataIntegrityViolationException::class.java)
            assertQueryCount().hasFailed(1)
                .hasSuccessful(1)
                .hasInserted(2)
        }

        @Test
        fun `should allow indexing lock on entries with different target`() {
            // given
            val indexing = Indexing.start(source = "whatever", target = ALL_TARGET)
            indexing.lock()
            val indexing2 = Indexing.start(source = "whatever", target = "another target")
            indexing2.lock()

            // when & then
            assertThatNoException().isThrownBy {
                indexingRepository.save(indexing)
                indexingRepository.save(indexing2)
            }
            assertQueryCount()
                .hasSuccessful(2)
                .hasInserted(2)
        }

        @Test
        fun `should not throw exception when saving two entries without a lock and one with lock`() {
            // given
            val indexing = Indexing.start(source = "whatever", target = "first target")
            indexing.lock()
            val indexing2 = Indexing.start(source = "whatever", target = "second target")
            indexing2.lock()

            // when
            indexingRepository.save(indexing)
            indexingRepository.save(indexing2)

            // then
            assertThat(indexingRepository.count()).isEqualTo(2)
            assertQueryCount().hasInserted(2)
        }

        @Test
        fun `should update and unlock started indexing`() {
            // given
            val indexing = Indexing.startAll(source = "whatever", )
            indexing.lock()

            // when
            val saved = indexingRepository.save(indexing)

            // then
            assertQueryCount().hasInserted(1)

            // when
            saved.finish(
                IndexingStatistics(
                    17, 17, 17, 17,
                    mapOf("exclusion 1 " to 2, "exclusion 2" to 3),
                    mapOf("error 1 " to 1, "error 2" to 2)
                )
            )
            saved.unlock()
            val updated = indexingRepository.save(saved)

            // then
            assertThat(updated)
                .extracting(Indexing::status, { it.lock })
                .containsExactly(IndexingStatus.FINISHED, null)
            assertThat(updated.finishedDate).isNotNull().isAfter(updated.startedDate)
            assertQueryCount().hasUpdated(1)
        }
    }

    @Nested
    @Sql("/sql/indexing_data_for_select.sql")
    inner class AdvancedOperattionsIndexingRepositoryTest {

        @Test
        fun `should find all locked`() {
            assertThat(indexingRepository.findAllLocked().size).isEqualTo(2)
        }

        @Test
        fun `should find all in progress`() {
            assertThat(indexingRepository.findAllInProgress().size).isEqualTo(9)
        }

        @Test
        fun `should find all locked for given target`() {
            // when
            val result = indexingRepository.findByLockIsNotNullAndTarget(ALL_TARGET)

            //then
            assertThat(result).isPresent
            val indexing = result.get()
            assertThat(indexing.status).isEqualTo(IndexingStatus.IN_PROGRESS)
            assertThat(indexing.target).isEqualTo(ALL_TARGET)
            assertThat(indexing.lock)
                .isNotNull()
                .extracting { it!!.getId() }
                .isNotNull()
        }

        @Test
        fun `should find latest for given target and status`() {
            // when
            val result = indexingRepository.findFirstByTargetAndStatusOrderByStartedDateDesc(ALL_TARGET, IndexingStatus.IN_PROGRESS)

            // then
            assertThat(result).isPresent
            val indexing = result.get()
            assertThat(indexing.startedDate).isAfter(LocalDateTime.now().minusMinutes(5))
            assertThat(indexing)
                .extracting(Indexing::target, Indexing::status)
                .containsExactly(ALL_TARGET, IndexingStatus.IN_PROGRESS)
        }
    }

}