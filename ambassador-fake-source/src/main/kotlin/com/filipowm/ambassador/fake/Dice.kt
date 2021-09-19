package com.filipowm.ambassador.fake

import java.util.function.Supplier
import kotlin.random.Random

internal class Dice<T>(private val sides: Int, vararg suppliers: Supplier<T>) {

    private val random = Random(System.currentTimeMillis())
    private val suppliers: List<Supplier<T>>

    init {
        if (sides < 3) {
            throw IllegalStateException("Dice must have at least 3 sides!")
        }
        if (suppliers.size < sides) {
            throw IllegalStateException("Each side must have associated data supplier")
        }
        this.suppliers = suppliers.asList()
    }

    fun roll(): Int = random.nextInt(sides) + 1

    fun rollForData(): T = suppliers[roll() - 1].get()
}
