package com.roche.ambassador.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Duration

class DurationExtensionsTest {

    @Test
    fun `should convert to human readable format`() {
        assertThat(Duration.ofHours(10).plusMinutes(21).plusSeconds(40).toHumanReadable())
            .isEqualTo("10h 21m 40s")
        assertThat(Duration.ofDays(30).plusMillis(31).toHumanReadable())
            .isEqualTo("720h 0.031s")
    }
}