package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.advisor.configuration.RulesProperties
import com.roche.ambassador.model.feature.TopicsFeature
import org.junit.jupiter.api.Test

class DslTest {

    @Test
    fun `should add problem when has clause is true`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "topics.size"
        }
        // then
        assertThat(advice).problems().has("topics.size")
    }

    @Test
    fun `should not add problem when has clause is false`() {
        // when
        val advice = testAdvise {
            anyAlwaysFalse() then "test"
        }
        // then
        assertThat(advice).hasProblemsSize(0)
    }

    @Test
    fun `should verify multiple rules`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() and { false } then "test1"
            anyAlwaysTrue() and { true } then "test2"
            anyAlwaysTrue() and { true } and { false } then "test3"
            anyAlwaysTrue() and { true } and { true } then "test4"
        }
        // then
        assertThat(advice).problems().hasNames("test2", "test4")
    }

    @Test
    fun `should add plain argument to problem`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with "1"
        }
        // then
        assertThat(advice).problems().has("test", "1")
    }

    @Test
    fun `should add multiple arguments to problem`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with listOf(1, 2)
        }
        // then
        assertThat(advice).problems().has("test", "1, 2")
    }

    @Test
    fun `should add argument from project to problem`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with { name }
        }
        // then
        assertThat(advice).problems().has("test", advice.project.name)
    }

    @Test
    fun `should match first rule in match first clause`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysFalse() then "test1"
                anyAlwaysTrue() then "test2"
                anyAlwaysTrue() then "test3"
            }
        }
        // then
        assertThat(advice).problems().has("test2")
    }

    @Test
    fun `should not add problem when action is do nothing`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysTrue().thenDoNothing()
                anyAlwaysTrue() then "test2"
            }
        }
        // then
        assertThat(advice).hasProblemsSize(0)
    }

    @Test
    fun `should not execute rules when rule is disabled`() {
        // when
        val advice = testAdvise {
            whenEnabled(RulesProperties.Rule(false)) {
                anyAlwaysTrue() then "test1"
                matchFirst {
                    anyAlwaysTrue() then "test2"
                }
            }
        }
        // then
        assertThat(advice).hasNoProblems()
    }

    @Test
    fun `should execute rule when rule is enabled`() {
        // when
        val advice = testAdvise {
            whenEnabled(RulesProperties.Rule(true)) {
                anyAlwaysTrue() then "test1"
                matchFirst {
                    anyAlwaysTrue() then "test2"
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test1", "test2")
    }

    @Test
    fun `should check if nested rule is enabled`() {
        // when
        val advice = testAdvise {
            whenEnabled(RulesProperties.Rule(true)) {
                anyAlwaysTrue() then "test1"
                whenEnabled(RulesProperties.Rule(false)) {
                    anyAlwaysTrue() then "test2"
                    whenEnabled(RulesProperties.Rule(true)) {
                        anyAlwaysTrue() then "test3" // disabled cause nested
                    }
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test1")
    }

    @Test
    fun `should match first rule outside nested match first clause`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysFalse() then "test1"
                matchFirst {
                    anyAlwaysFalse() then "test2"
                }
                anyAlwaysTrue() then "test3"
            }
        }
        // then
        assertThat(advice).problems().has("test3")
    }

    @Test
    fun `should match first rule in nested match first clause`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysFalse() then "test1"
                matchFirst {
                    anyAlwaysFalse() then "test2"
                    anyAlwaysTrue() then "test3"
                }
                anyAlwaysTrue() then "test4"
            }
        }
        // then
        assertThat(advice).problems().hasSize(1).has("test3")
    }

    @Test
    fun `should match first in deeply nested match first clause`() {
        // when
        val advice = testAdvise {
            matchFirst {
                matchFirst {
                    matchFirst {
                        matchFirst {
                            anyAlwaysFalse() then "test1"
                            anyAlwaysTrue() then "test2"
                        }
                    }
                    matchFirst {
                        anyAlwaysFalse() then "test3"
                        anyAlwaysTrue() then "test4"
                    }
                    anyAlwaysTrue() then "test5"
                }
                anyAlwaysTrue() then "test6"
            }
        }
        // then
        assertThat(advice).problems().hasSize(1).has("test2")
    }

    @Test
    fun `should match all rules`() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test1"
            matchFirst {
                anyAlwaysFalse() then "test2"
                anyAlwaysTrue() then "test3"
            }
            matchFirst {
                anyAlwaysTrue() then "test4"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test1", "test3", "test4")
    }

    @Test
    fun `should use project predicate in has`() {
        // when
        val advice = testAdvise {
            has { topics.size > -1 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should use project predicate in has not`() {
        // when
        val advice = testAdvise {
            hasNot { topics.size < 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should use value predicate in that match first`() {
        // when
        val advice = testAdvise {
            matchFirst( { topics } ) {
                that { size > -1 } then "test"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should use value predicate in that not match first`() {
        // when
        val advice = testAdvise {
            matchFirst( { topics } ) {
                thatNot { size < 0 } then "test"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should match first in nested match first value`() {
        // when
        val advice = testAdvise {
            matchFirst( { topics } ) {
                that { size < 0 } then "test1"
                matchFirst {
                    anyAlwaysFalse() then "test2"
                    anyAlwaysTrue() then "test3"
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test3")
    }

    @Test
    fun `should use feature in that`() {
        // when
        val advice = testAdvise {
            has(TopicsFeature::class) that { size > 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should use feature in that not`() {
        // when
        val advice = testAdvise {
            has(TopicsFeature::class) thatNot { size < 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun `should match first in match first feature`() {
        // when
        val advice = testAdvise {
            matchFirst(TopicsFeature::class) {
                that { size < 0 } then "test1"
                that { size > 0 } then "test2"
                that { size > -1 } then "test3"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test2")
    }

    @Test
    fun `should match all in with feature`() {
        // when
        val advice = testAdvise {
            with(TopicsFeature::class) {
                that { size < 0 } then "test1"
                that { size > 0 } then "test2"
                that { size > -1 } then "test3"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test2", "test3")
    }

    @Test
    fun `should match first with feature value`() {
        // when
        val advice = testAdvise {
            matchFirst(TopicsFeature::class, { size }) {
                that { this < 0 } then "test1"
                that { this > 0 } then "test2"
                that { this > -1 } then "test3"
            }
        }
        // then
        assertThat(advice).problems().hasNames("test2")
    }

    @Test
    fun `should match all in or in match first`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysFalse() then "test1"
                or {
                    anyAlwaysTrue() then "test2"
                    anyAlwaysTrue() then "test3"
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test2", "test3")
    }


    @Test
    fun `should match first in matchfirst and not or`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysTrue() then "test1"
                or {
                    anyAlwaysTrue() then "test2"
                    anyAlwaysTrue() then "test3"
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test1")
    }

    @Test
    fun `should handle complex or in match first`() {
        // when
        val advice = testAdvise {
            matchFirst {
                anyAlwaysFalse() then "test1"
                or {
                    matchFirst {
                        anyAlwaysTrue() then "test2"
                    }
                    matchFirst {
                        anyAlwaysFalse() then "test3"
                    }
                    anyAlwaysTrue() then "test4"
                    matchFirst {
                        anyAlwaysTrue() then "test5"
                    }
                }
            }
        }
        // then
        assertThat(advice).problems().hasNames("test2", "test4", "test5")
    }

}