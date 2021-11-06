package com.roche.ambassador.extensions

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class EnumExtensionsTest {

    @ParameterizedTest
    @EnumSource(TestEnum::class)
    internal fun `should pretty print enum name`(entry: TestEnum) {
        Assertions.assertThat(entry.toPrettyString()).isEqualTo(entry.expected)
    }

    internal enum class TestEnum(val expected: String) {
        FIRST("First"),
        SECOND_ENTRY("Second Entry"),
        Third_entry("Third Entry"),
        fourth("Fourth")
    }
}
