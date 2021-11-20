package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Feature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier
import java.util.function.Consumer

class FeatureNameLookupTest {

    @Test
    fun `should all features have defined names`() {
        // given
        val reflections = Reflections("com.roche.ambassador.model.feature")
        val classes = reflections.getSubTypesOf(Feature::class.java)
            .filterNot { Modifier.isInterface(it.modifiers) || Modifier.isAbstract(it.modifiers) }
            .map { FeatureNameLookup.getFeatureName(it.kotlin) to it }

        // expect
        assertThat(classes).allSatisfy(Consumer {
            assertThat(it.first)
                .describedAs("Feature %s should have name defined", it.second.simpleName)
                .isPresent
        })

        assertThat(classes.map { it.first.get() }.toSet())
            .describedAs("Feature names must be unique")
            .hasSize(classes.size)
    }
}