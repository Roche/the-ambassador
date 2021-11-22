package com.roche.ambassador.fake

import java.util.function.Supplier
import kotlin.random.Random

internal class Dice<T>(val sides: Int, vararg suppliers: Supplier<T>) {

    private val random = Random(System.currentTimeMillis())
    private val suppliers: List<Supplier<T>>

    constructor(vararg suppliers: Supplier<T>) : this(suppliers.size, *suppliers)

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

    fun rollForData(times: Int): List<T> = (0..times).map { rollForData() }

    companion object {
        inline fun <reified T : Enum<*>> ofEnum(): Dice<T> {
            val values = enumValues<T>()
            val suppliers = values
                .map { SingleValueSupplier(it) }
            return Dice(suppliers.size, *suppliers.toTypedArray())
        }
    }

    internal class SingleValueSupplier<T>(private val value: T) : Supplier<T> {
        override fun get(): T = value
    }
}
