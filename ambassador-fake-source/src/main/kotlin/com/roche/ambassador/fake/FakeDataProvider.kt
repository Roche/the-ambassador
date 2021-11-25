package com.roche.ambassador.fake

import com.github.javafaker.Faker
import com.github.javafaker.service.RandomService
import com.roche.ambassador.extensions.sha256
import com.roche.ambassador.extensions.toDate
import com.roche.ambassador.extensions.toLocalDate
import com.roche.ambassador.model.files.RawFile
import com.roche.ambassador.model.group.Group
import com.roche.ambassador.model.project.AccessLevel
import com.roche.ambassador.model.project.Contributor
import com.roche.ambassador.model.project.Member
import com.roche.ambassador.model.Visibility
import java.time.LocalDate
import java.util.*
import java.util.function.Supplier
import kotlin.math.floor

class FakeDataProvider {

    private val faker = Faker(Locale.ENGLISH, RandomService())
    private val nameDice = Dice(faker.witcher()::monster,
        faker.witcher()::location,
        faker.pokemon()::name, faker.harryPotter()::spell, faker.beer()::name,
        faker.lordOfTheRings()::location
    )
    private val parentNameDice = Dice(faker.aviation()::aircraft,
        faker.starTrek()::character, faker.dragonBall()::character
    )
    private val descriptionDice = Dice(faker.witcher()::monster, faker.witcher()::location,
        faker.medical()::diseaseName, faker.rockBand()::name, faker.aviation()::airport,
        faker.chuckNorris()::fact, faker.programmingLanguage()::name, faker.gameOfThrones()::character,
        faker.gameOfThrones()::quote, faker.space()::star
    )
    private val languageDice = Dice.ofEnum<Language>()
    private val accessLevelDice = Dice.ofEnum<AccessLevel>()
    private val branchTypeDice = Dice.ofEnum<BranchType>()
    private val groupTypeDice = Dice.ofEnum<Group.Type>()

    fun name(): String = nameDice.rollForData()

    fun parentName(): String = parentNameDice.rollForData()

    fun description(): String {
        return faker.lorem().sentence(200).substring(0, 1020)
    }

    fun tags(): List<String> {
        return listOf(
            faker.company().industry(),
            languageDice.rollForData().value,
            faker.harryPotter().spell(),
            faker.music().genre()
        ).map { it.replace(" ", "-") }
    }

    fun nextInt(min: Int = 0, max: Int = 100): Int {
        return faker.number().numberBetween(min, max)
    }

    fun nextLong(min: Long = 0, max: Long = 10000): Long {
        return faker.number().numberBetween(min, max)
    }

    fun nextDouble(min: Int = 0, max: Int = 1000, maxNumberOfDecimals: Int = 2): Double {
        return faker.number().randomDouble(maxNumberOfDecimals, min, max)
    }

    fun bool(): Boolean = faker.bool().bool()

    fun date(from: LocalDate = LocalDate.now().minusYears(5), to: LocalDate = LocalDate.now()): LocalDate = faker.date().between(from.toDate(), to.toDate()).toLocalDate()

    fun visibility(): Visibility {
        return Visibility.values()[faker.random().nextInt(0, Visibility.values().size - 1)]
    }

    fun projectUrl(): String {
        return faker.internet().url()
    }

    fun avatarUrl(): String {
        return faker.internet().avatar()
    }

    fun <T> generate(min: Int = 0, max: Int, generator: Supplier<T>): List<T> {
        return (0..nextInt(min = min, max = max)).map { generator.get() }
    }

    fun languages(): Map<String, Float> {
        val langs = languageDice.rollForData(nextInt(1, 6)).map { it.value }
        val counts = generateDoubles(langs.size, 1, 100.0).map { it.toFloat() }
        return langs.zip(counts).toMap()
    }

    fun contributor(): Contributor {
        return Contributor(
            faker.name().fullName(),
            faker.internet().emailAddress(),
            nextInt(min = 1, max = 500),
            avatarUrl()
        )
    }

    fun member(): Member {
        return Member(
            nextLong(1, 15000),
            faker.name().fullName(),
            faker.internet().emailAddress(),
            nameDice.rollForData(),
            accessLevelDice.rollForData()
        )
    }

    fun defaultBranch(): String? {
        return withBinaryChance(95, { "main" }, { null })
    }

    fun groupType(): Group.Type = groupTypeDice.rollForData()

    private fun generateDoubles(count: Int, maxDecimals: Int, total: Double): List<Double> {
        var subtotal = total
        val numbers = mutableListOf<Double>()
        (count - 1..0).forEach {
            val max = floor(subtotal - count).toInt()
            val num = faker.number().randomDouble(maxDecimals, 1, max)
            subtotal -= num
            numbers.add(num)
        }
        numbers.add(subtotal)
        return numbers.toList()
    }

    fun createFile(): RawFile {
        val mainDescription = faker.lorem()
            .paragraphs(nextInt(1, 3))
            .flatMap { it.split(" ") }
        val randomParts = descriptionDice.rollForData(nextInt(1, descriptionDice.sides * 3))
        val content = (mainDescription + randomParts).shuffled().joinToString()
        return RawFile(true, content.sha256().orElse(null), "english", content.length.toLong(), faker.internet().url(), content)
    }

    fun branch(): String {
        fun randomBranch(): String {
            val type = branchTypeDice.rollForData().name.toLowerCase()
            return "$type/${faker.ancient().god()}-${faker.ancient().hero()}-${faker.ancient().primordial()}"
        }
        return withBinaryChance(90, { "main" }, { randomBranch() })!!
    }

    fun <T> withBinaryChance(chancePercentage: Int, onSuccess: () -> T?, onFailure: () -> T?): T? {
        require(chancePercentage > 0.0)
        require(chancePercentage <= 100.0)
        return if (faker.number().numberBetween(0, 100) <= chancePercentage) {
            onSuccess()
        } else {
            onFailure()
        }
    }
}
