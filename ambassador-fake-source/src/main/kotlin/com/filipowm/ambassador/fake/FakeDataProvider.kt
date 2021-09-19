package com.filipowm.ambassador.fake

import com.filipowm.ambassador.extensions.toDate
import com.filipowm.ambassador.extensions.toLocalDate
import com.filipowm.ambassador.model.project.Visibility
import com.github.javafaker.Faker
import com.github.javafaker.service.RandomService
import java.time.LocalDate
import java.util.*

class FakeDataProvider {

    private val faker = Faker(Locale.ENGLISH, RandomService())
    private val nameDice = Dice(
        6, faker.witcher()::monster,
        faker.witcher()::location,
        faker.pokemon()::name, faker.harryPotter()::spell, faker.beer()::name,
        faker.lordOfTheRings()::location
    )

    fun name(): String = nameDice.rollForData()
    fun description(name: String = "", tags: List<String> = listOf()): String {
        return faker.lorem().sentence(200).substring(0, 1020)
    }

    fun tags(): List<String> {
        return listOf()
    }

    fun nextInt(min: Int = 0, max: Int = 100): Int {
        return faker.number().numberBetween(min, max)
    }

    fun nextLong(min: Long = 0, max: Long = 10000): Long {
        return faker.number().numberBetween(min, max)
    }

    fun date(from: LocalDate = LocalDate.now().minusYears(5), to: LocalDate = LocalDate.now()): LocalDate = faker.date().between(from.toDate(), to.toDate()).toLocalDate()

    fun visibility(): Visibility {
        return Visibility.values()[faker.random().nextInt(0, Visibility.values().size - 1)]
    }

    fun projectUrl(name: String): String? {
        return faker.internet().url()
    }

    fun avatarUrl(): String? {
        return faker.internet().avatar()
    }

    fun defaultBranch(): String? {
        if (faker.number().numberBetween(0, 100) <= 95) {
            return "main"
        }
        return null
    }
}
