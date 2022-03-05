package com.roche.ambassador.model.score

import com.roche.ambassador.model.Score
import com.roche.ambassador.model.extensions.*
import com.roche.ambassador.model.feature.*
import com.roche.ambassador.model.stats.Timeline
import com.roche.ambassador.model.stats.TimelineGenerator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

@DisplayName("Criticality Score tests")
class CriticalityScorePolicyTest {

    // must leave this test to ensure other tests are correct
    @Test
    fun `test calculator should return 0 for default data`() {
        // given
        val data = CriticalityData()

        // when
        val result = SimpleCriticalityScoreCalculator.calculate(data)

        // then
        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `test calculator should return correct value for complete data`() {
        // given
        val data = CriticalityData(
            50, 42, 224,
            LocalDate.now().minusMonths(127), LocalDate.now(),
            TimelineGenerator.withWeekAverage(20.0, 52),
            TimelineGenerator.withTotalEvents(15, startDate = LocalDate.now().minusMonths(11)),
            TimelineGenerator.withTotalEvents(50, startDate = LocalDate.now().minusMonths(2)),
        )

        // when
        val result = SimpleCriticalityScoreCalculator.calculate(data)

        val exact = 0.697
        // then result is based on manual calculation based on algorithm defined above
        assertThat(result).isBetween(exact * 0.9, exact * 1.1)

        // when calculate criticality score using policy
        val criticality = CriticalityScorePolicy.calculateScoreOf(data.toFeatures())

        // then all expected features and subscores are used
        assertThat(criticality)
            .hasScoresSize(1)
            .hasValue(result)
            .hasFeatures(
                LastActivityDateFeature::class,
                CommitsFeature::class,
                CreatedDateFeature::class,
                ContributorsFeature::class,
                IssuesFeature::class,
                ReleasesFeature::class,
                CommentsFeature::class
            )
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "10, 0.1024",
        "4999, 0.3636",
        "5000, 0.3636",
        "5001, 0.3636",
    )
    fun `should use contributors count only`(contributorsCount: Int, expected: Double) {
        // given
        val data = CriticalityData(contributorsCount = contributorsCount)

        // expect
        assertWithData(data, expected)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "10, 0.0256",
        "4999, 0.0909",
        "5000, 0.0909",
        "5001, 0.0909",
    )
    fun `should use open issues count only`(updatedIssues: Int, expected: Double) {
        // given
        val data = CriticalityData(updatedIssuesIn90Days = updatedIssues)

        // expect
        assertWithData(data, expected)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 0",
        "10, 0.0256",
        "4999, 0.0909",
        "5000, 0.0909",
        "5001, 0.0909",
    )
    fun `should use closed issues count only`(closedIssues: Int, expected: Double) {
        // given
        val data = CriticalityData(closedIssuesIn90Days = closedIssues)

        // expect
        assertWithData(data, expected)
    }

    @ParameterizedTest
    @MethodSource("commitTimelines")
    fun `should use commit frequency only`(commits: Timeline, expected: Double) {
        // given
        val data = CriticalityData(commits = commits)

        // when
        val actual = CriticalityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data).isBetween(expected * .9, expected * 1.1)
    }

    @ParameterizedTest
    @MethodSource("releaseTimelines")
    fun `should use recent releases count only`(releases: Timeline, expected: Double) {
        // given
        val data = CriticalityData(releases = releases)

        // expect
        assertWithData(data, expected)
    }

    @ParameterizedTest
    @MethodSource("dates")
    fun `should use last activity date only`(date: LocalDate, expected: Double) {
        // given
        val data = CriticalityData(lastActivityDate = date)

        // expect
        assertWithData(data, -expected)
    }

    @ParameterizedTest
    @MethodSource("dates")
    fun `should use created date only`(date: LocalDate, expected: Double) {
        // given
        val data = CriticalityData(createdDate = date)

        // expect
        assertWithData(data, expected)
    }

    private fun assertWithData(/* given */data: CriticalityData, expected: Double) {
        // when
        val actual = CriticalityScorePolicy.calculateScoreOf(data.toFeatures())

        // then doing double cross-check
        assertThat(actual).hasCorrectValue(data).hasValueRounded(expected, 4)
    }

    private companion object MethodSources {
        @JvmStatic
        fun commitTimelines(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(TimelineGenerator.withWeekAverage(0.0, 52), 0),
                Arguments.of(TimelineGenerator.withWeekAverage(5.0, 52), .0467),
                Arguments.of(TimelineGenerator.withWeekAverage(50.0, 52), .1024),
                Arguments.of(TimelineGenerator.withWeekAverage(500.0, 52), .1628),
                Arguments.of(TimelineGenerator.withWeekAverage(500.0, 104), .1628),
                Arguments.of(TimelineGenerator.withWeekAverage(1000.0, 52), .1818),
                Arguments.of(TimelineGenerator.withWeekAverage(2000.0, 52), .1818),
            )
        }
        @JvmStatic
        fun releaseTimelines(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(TimelineGenerator.withTotalEvents(0, startDate = LocalDate.now().minusYears(0)), 0.0),
                Arguments.of(TimelineGenerator.withTotalEvents(24, startDate = LocalDate.now().minusYears(2)), .0707),
                Arguments.of(TimelineGenerator.withTotalEvents(12, startDate = LocalDate.now().minusYears(1)), .0707),
                Arguments.of(TimelineGenerator.withTotalEvents(26, startDate = LocalDate.now().minusYears(1)), .0909),
                Arguments.of(TimelineGenerator.withTotalEvents(26, startDate = LocalDate.now().minusMonths(6)), .0909),
                Arguments.of(TimelineGenerator.withTotalEvents(27, startDate = LocalDate.now().minusYears(1)), .0909),
                Arguments.of(TimelineGenerator.withTotalEvents(50, startDate = LocalDate.now().minusYears(1)), .0909),
            )
        }

        @JvmStatic
        fun dates(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(LocalDate.now().minusYears(10), .1818),
                Arguments.of(LocalDate.now().minusDays(121), .061),
                Arguments.of(LocalDate.now().minusDays(120), .061),
                Arguments.of(LocalDate.now().minusDays(119), .0526),
                Arguments.of(LocalDate.now().minusDays(60), .0417),
                Arguments.of(LocalDate.now().minusMonths(1).plusDays(1), .0),
                Arguments.of(LocalDate.now().minusDays(5), .0),
                Arguments.of(LocalDate.now(), .0)
            )
        }
    }

    private fun ObjectAssert<Score>.hasCorrectValue(data: CriticalityData): ObjectAssert<Score> = hasCorrectValueBasedOnCalculator(data, SimpleCriticalityScoreCalculator)
}
