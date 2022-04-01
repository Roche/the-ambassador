package com.roche.ambassador.advisor.dsl

import com.roche.ambassador.model.feature.TopicsFeature
import org.junit.jupiter.api.Test

class DslTest {

    @Test
    fun shouldAddProblemWhenHasClauseIsTrue() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "topics.size"
        }
        // then
        assertThat(advice).problems().has("topics.size")
    }

    @Test
    fun shouldNotAddProblemWhenHasClauseIsFalse() {
        // when
        val advice = testAdvise {
            anyAlwaysFalse() then "test"
        }
        // then
        assertThat(advice).hasProblemsSize(0)
    }

    @Test
    fun shouldAddPlainArgumentToProblem() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with "1"
        }
        // then
        assertThat(advice).problems().has("test", "1")
    }

    @Test
    fun shouldAddMultipleArgumentsToProblem() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with listOf(1, 2)
        }
        // then
        assertThat(advice).problems().has("test", "1, 2")
    }

    @Test
    fun shouldAddArgumentFromProjectToProblem() {
        // when
        val advice = testAdvise {
            anyAlwaysTrue() then "test" with { name }
        }
        // then
        assertThat(advice).problems().has("test", advice.project.name)
    }

    @Test
    fun shouldMatchFirstRuleInMatchFirstClause() {
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
    fun shouldMatchFirstRuleOutsideNestedMatchFirstClause() {
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
    fun shouldMatchFirstRuleInNestedMatchFirstClause() {
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
    fun shouldMatchFirstInDeeplyNestedMatchFirstClause() {
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
    fun shouldMatchAllRules() {
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
    fun shouldUseProjectPredicateInHas() {
        // when
        val advice = testAdvise {
            has { topics.size > -1 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun shouldUseProjectPredicateInHasNot() {
        // when
        val advice = testAdvise {
            hasNot { topics.size < 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun shouldUseValuePredicateInThatMatchFirst() {
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
    fun shouldUseValuePredicateInThatNotMatchFirst() {
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
    fun shouldMatchFirstInNestedMatchFirstValue() {
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
    fun shouldUseFeatureInThat() {
        // when
        val advice = testAdvise {
            has(TopicsFeature::class) that { size > 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun shouldUseFeatureInThatNot() {
        // when
        val advice = testAdvise {
            has(TopicsFeature::class) thatNot { size < 0 } then "test"
        }
        // then
        assertThat(advice).problems().hasNames("test")
    }

    @Test
    fun shouldMatchFirstInMatchFirstFeature() {
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
    fun shouldMatchAllInWithFeature() {
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
    fun shouldMatchAllInOrInMatchFirst() {
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
    fun shouldMatchFirstInMatchFirstAndNotOr() {
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
    fun shouldHandleComplexOrInMatchFirst() {
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