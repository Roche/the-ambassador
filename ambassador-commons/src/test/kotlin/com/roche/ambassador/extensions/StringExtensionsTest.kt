package com.roche.ambassador.extensions

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class StringExtensionsTest {

    @ParameterizedTest
    @CsvSource(
        "random input, Random Input",
        "random inPUT, Random InPUT",
        "random, Random",
        "RANDOM, RANDOM",
        "random_input,Random_input"
    )
    fun `should capitalize string`(input: String, expected: String) {
        Assertions.assertThat(input.capitalize()).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "random input, Random Input",
        "random inPUT, Random Input",
        "random, Random",
        "RANDOM, Random",
        "random_input,Random_input"
    )
    fun `should capitalize fully string`(input: String, expected: String) {
        Assertions.assertThat(input.capitalizeFully()).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "random input, -1, ",
        "random input, 0, ",
        "random input, 1, ",
        "random input, 6, random",
        "random input, 7, random",
        "random input, 8, random",
        "random input, 11, random input",
        "random input, 12, random input",
    )
    fun `should substring with full words`(input: String, maxLength: Int, expected: String?) {
        val exp = expected ?: ""
        Assertions.assertThat(input.substringWithFullWords(0, maxLength)).isEqualTo(exp)
    }

    @ParameterizedTest
    @CsvSource(
        "random input, randomInput",
        "random iNPUT, randomInput",
        "random, random",
        "RANDOM, random",
        "randomInput, randomInput",
        "randomINPUT, randomInput",
        "random_input,randomInput",
        "random-input,randomInput",
    )
    fun `should transform to camel case`(input: String, expected: String) {
        Assertions.assertThat(input.toCamelCase()).isEqualTo(expected)
    }

}