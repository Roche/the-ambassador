package com.roche.ambassador

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TheAmbassadorTest {

    @Test
    fun `context is up`() {
        assertThat(true).isTrue
    }

}