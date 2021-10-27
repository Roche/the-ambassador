package com.roche.ambassador.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class NumericExtensionsTest {

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "0.1, 0.1",
        "0.123, 0.123",
        "0.1555, 0.156",
        "0.1544, 0.154",
        "0.9999, 1",
    )
    fun `should round number`(number: Double, expected: Double) {
        assertThat(number.round(3)).isEqualTo(expected)
    }
}