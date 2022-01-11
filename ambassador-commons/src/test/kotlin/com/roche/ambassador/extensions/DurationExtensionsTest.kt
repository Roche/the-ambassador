package com.roche.ambassador.extensions

import com.roche.ambassador.Durations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class DurationExtensionsTest {

    companion object {
        private val THURSDAY: LocalDateTime = LocalDate.of(2022, 1, 8).atStartOfDay()
    }

    @Test
    fun `should convert to human readable format`() {
        assertThat(Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4).toHumanReadable())
            .isEqualTo("1d 2h 3m 4s")
        assertThat(Duration.ofHours(10).plusMinutes(21).plusSeconds(40).toHumanReadable())
            .isEqualTo("10h 21m 40s")
        assertThat(Duration.ofDays(30).plusMillis(31).toHumanReadable())
            .isEqualTo("30d")
        assertThat(Duration.ofDays(30).plusMillis(31).toHumanReadable(withMillis = true))
            .isEqualTo("30d 31ms")
    }

    @Test
    fun `should calculate duration between dates without weekends`() {
        // given
        val start = THURSDAY.plusHours(22) // thu 22:00
        val end = THURSDAY.plusDays(4).plusHours(10) // mon 10:00

        // expect
        assertThat(Durations.between(start, end, includeWeekends = false))
            .isEqualTo(Duration.ofHours(2 + 10 + 24 + 24))
    }

    @Test
    fun `should calculate duration between dates with weekends`() {
        // given
        val start = THURSDAY.plusHours(22)
        val end = THURSDAY.plusDays(4).plusHours(10) // mon 10:00

        // expect
        assertThat(Durations.between(start, end))
            .isEqualTo(Duration.ofHours(2 + 10 + 24 + 24 + 24))
    }

    @Test
    fun `should calculate duration between dates when on same day`() {
        // given
        val start = THURSDAY.plusHours(1)
        val end = THURSDAY.plusHours(3)

        // expect
        assertThat(Durations.between(start, end))
            .isEqualTo(Duration.ofHours(2))
    }
}
