package com.filipowm.ambassador.model.score

import com.filipowm.ambassador.model.Score
import com.filipowm.ambassador.model.extensions.*
import com.filipowm.ambassador.model.feature.*
import com.filipowm.ambassador.model.files.Documentation
import com.filipowm.ambassador.model.stats.Timeline
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
import kotlin.math.ln
import kotlin.math.round
import kotlin.reflect.KClass

@DisplayName("Activity Score tests")
class ActivityScorePolicyTest {

    // must leave this test to ensure other tests are correct
    @Test
    fun `test calculator should return 0 for default data`() {
        // given
        val data = ActivityData()

        // when
        val result = SimpleActivityScoreCalculator.calculate(data)

        // then
        assertThat(result).isEqualTo(0.0)
    }

    @Test
    fun `test calculator should return correct value for complete data`() {
        // given
        val documentation = Documentation.create(true, 200)
        val commitsTimeline = TimelineGenerator.withWeekAverage(50.0, 12)
        val data = ActivityData(
            10, 10, 10, LocalDate.now().minusDays(2), LocalDate.now().minusMonths(2),
            false, documentation, documentation, documentation, documentation, fairy.textProducer().word(30), commitsTimeline
        )

        // when
        val result = SimpleActivityScoreCalculator.calculate(data)

        // then result is based on manual calculation based on algorithm defined above
        assertThat(result).isEqualTo(1526.0)

        // when calculate activity score using policy
        val activity = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then all expected features and subscores are used
        assertThat(activity.allFeatures().map { it::class })
            .containsExactlyInAnyOrder(
                StarsFeature::class,
                ForksFeature::class,
                LastActivityDateFeature::class,
                CommitsFeature::class,
                CreatedDateFeature::class,
                ContributingGuideFeature::class,
                ReadmeFeature::class,
                LicenseFeature::class,
                ChangelogFeature::class,
                DescriptionFeature::class,
                IssuesFeature::class
            )
        assertThat(activity).hasScoresSize(3)
            .hasValue(result)
    }

    @Test
    fun `should use stars only`() {
        // given
        val data = ActivityData(stars = 10)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
            .hasFeature(StarsFeature::class)
    }

    @Test
    fun `should use forks only`() {
        // given
        val data = ActivityData(forks = 10)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
            .hasFeature(ForksFeature::class)
    }

    @ParameterizedTest(name = "should use date {0}")
    @MethodSource("lastActivityDates")
    fun `should use last activity date only to boost score`(date: LocalDate) {
        // given
        val data = ActivityData(lastActivityDate = date)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
    }

    @ParameterizedTest
    @MethodSource("commitTimelines")
    fun `should add bonus multiplier based on commits count`(timeline: Timeline, averageCommits: Double, expectedScoreMoreless: Double) {
        val data = ActivityData(commitsTimeline = timeline)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
        // here expected score is within some boundaries,
        // because Poisson distribution cannot give ideal mean,
        // especially for not big amount of samples and low expected mean
        assertThat(actual.value())
            .isGreaterThanOrEqualTo(expectedScoreMoreless * .5)
            .isLessThanOrEqualTo(expectedScoreMoreless * 1.5)
    }

    @ParameterizedTest
    @MethodSource("lastActivityAndCreatedDates")
    fun `should add bonus multiplier for new, active projects`(createdDate: LocalDate, lastActivityDate: LocalDate) {
        // given
        val data = ActivityData(createdDate = createdDate, lastActivityDate = lastActivityDate)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
    }

    @Test
    fun `should apply logarithmic scale for very active projects`() {
        // given
        val stars = 100000
        val data = ActivityData(stars = stars)
        val starsValue = stars * 2
        val expectedValue = round(3000 + ln(starsValue.toDouble()) * 100 - 50)

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
            .hasValue(expectedValue)
    }

    @Test
    fun `should penalize private projects`() {
        // given
        val stars = 1000
        val data = ActivityData(stars = stars, private = true)
        val expectedValue = (50 + stars * 2) * .3 - 50

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasCorrectValue(data)
            .hasValue(expectedValue)
            .hasFeature(VisibilityFeature::class)
    }

    @Test
    fun `should use description only`() {
        // given
        val data = ActivityData(description = fairy.textProducer().randomString(30))

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual)
            .hasCorrectValue(data)
            .hasValue(50.0)
            .hasFeature(DescriptionFeature::class)
    }

    @ParameterizedTest(name = "should boost by {0} when readme has length {1}")
    @CsvSource(
        "100,100",
        "100,101",
        "0,99",
        "0,0",
        "0,-1",
    )
    fun `should use readme only`(expectedBoost: Double, length: Int) {
        verifyDocumentationBoost(expectedBoost, length, ReadmeFeature::class) { ActivityData(readme = it) }
    }

    @ParameterizedTest(name = "should boost by {0} when contribution guuide has length {1}")
    @CsvSource(
        "100,101",
        "100,100",
        "0,99",
        "0,0",
        "0,-1",
    )
    fun `should use contribution guide only`(expectedBoost: Double, length: Int) {
        verifyDocumentationBoost(expectedBoost, length, ContributingGuideFeature::class) { ActivityData(contributionGuide = it) }
    }

    @ParameterizedTest(name = "should boost by {0} when license has length {1}")
    @CsvSource(
        "5,51",
        "5,50",
        "0,49",
        "0,0",
        "0,-1",
    )
    fun `should use license only`(expectedBoost: Double, length: Int) {
        verifyDocumentationBoost(expectedBoost, length, LicenseFeature::class) { ActivityData(license = it) }
    }

    @ParameterizedTest(name = "should boost by {0} when changelog has length {1}")
    @CsvSource(
        "10,51",
        "10,50",
        "0,49",
        "0,0",
        "0,-1",
    )
    fun `should use changelog only`(expectedBoost: Double, length: Int) {
        verifyDocumentationBoost(expectedBoost, length, ChangelogFeature::class) { ActivityData(changelog = it) }
    }

    private fun verifyDocumentationBoost(expectedBoost: Double, length: Int, expectedFeature: KClass<out FileFeature<*>>, dataProvider: (Documentation) -> ActivityData) {
        // given
        val exists = length >= 0
        val data = dataProvider.invoke(Documentation.create(exists, length))

        // when
        val actual = ActivityScorePolicy.calculateScoreOf(data.toFeatures())

        // then
        assertThat(actual).hasValue(expectedBoost)

        if (expectedBoost > 0) {
            assertThat(actual).hasFeature(expectedFeature)
        }
    }

    private companion object MethodSources {
        @JvmStatic
        fun lastActivityDates(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(LocalDate.now()),
                Arguments.of(LocalDate.now().minusYears(10)),
                Arguments.of(LocalDate.now().minusDays(50)),
                Arguments.of(LocalDate.now().minusDays(100)),
                Arguments.of(LocalDate.now().minusDays(101)),
            )
        }

        @JvmStatic
        fun commitTimelines(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(TimelineGenerator.withWeekAverage(0.0, 12), 0.0, 0.0),
                Arguments.of(TimelineGenerator.withWeekAverage(1.0, 12), 1.0, 0.0),
                Arguments.of(TimelineGenerator.withWeekAverage(2.0, 12), 2.0, 0.0),
                Arguments.of(TimelineGenerator.withWeekAverage(8.5, 12), 8.5, 39.28),
                Arguments.of(TimelineGenerator.withWeekAverage(10.5, 12), 10.5, 50.0),
            )
        }

        @JvmStatic
        fun lastActivityAndCreatedDates(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(LocalDate.now().minusYears(10), LocalDate.now().minusYears(9)),
                Arguments.of(LocalDate.now().minusDays(365), LocalDate.now().minusDays(365)),
                Arguments.of(LocalDate.now().minusDays(365), LocalDate.now().minusDays(360)),
                Arguments.of(LocalDate.now().minusDays(180), LocalDate.now().minusDays(180)),
                Arguments.of(LocalDate.now().minusDays(180), LocalDate.now().minusDays(170)),
                Arguments.of(LocalDate.now().minusDays(180), LocalDate.now().minusDays(2)),
                Arguments.of(LocalDate.now().minusDays(180), LocalDate.now()),
                Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().minusDays(15)),
                Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().minusDays(2)),
                Arguments.of(LocalDate.now().minusDays(15), LocalDate.now().minusDays(0)),
                Arguments.of(LocalDate.now().minusDays(0), LocalDate.now())
            )
        }
    }
}

fun ObjectAssert<Score>.hasCorrectValue(data: ActivityData): ObjectAssert<Score> {
    val expected = SimpleActivityScoreCalculator.calculate(data)
    extracting { it.value() }.isEqualTo(expected)
    return this
}