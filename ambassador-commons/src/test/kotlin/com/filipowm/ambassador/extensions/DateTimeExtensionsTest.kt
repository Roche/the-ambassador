package com.filipowm.ambassador.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

class DateTimeExtensionsTest {

    @ParameterizedTest
    @ValueSource(longs = [-1, 0, 1, 31, 365, 1000])
    fun `should calculate days until now`(diff: Long) {
        // expect
        assertThat(LocalDate.now().minusDays(diff).daysUntilNow()).isEqualTo(diff)
    }

    @ParameterizedTest
    @ValueSource(longs = [-1, 0, 1, 12, 200])
    fun `should calculate months until now`(diff: Long) {
        // expect
        assertThat(LocalDate.now().minusMonths(diff).monthsUntilNow()).isEqualTo(diff)
    }
}