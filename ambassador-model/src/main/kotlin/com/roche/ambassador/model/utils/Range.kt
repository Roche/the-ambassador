package com.roche.ambassador.model.utils

sealed class Range<T : Number>(protected val min: T, protected val max: T) {

    abstract fun adjust(input: T): T

    companion object {
        fun bound(min: Int, max: Int): BoundRange<Int> = BoundRange(min, max)
        fun bound(min: Double, max: Double): BoundRange<Double> = BoundRange(min, max)
        fun unbound(min: Int, max: Int): UnboundRange<Int> = UnboundRange(min, max)
        fun unbound(min: Double, max: Double): UnboundRange<Double> = UnboundRange(min, max)
    }
}

open class BoundRange<T : Number>(min: T, max: T) : Range<T>(min, max) {
    override fun adjust(input: T): T {
        return when {
            input.toDouble() <= min.toDouble() -> min
            input.toDouble() >= max.toDouble() -> max
            else -> input
        }
    }
}

open class UnboundRange<T : Number>(min: T, max: T) : Range<T>(min, max) {
    override fun adjust(input: T): T {
        return when {
            input.toDouble() < min.toDouble() -> min
            input.toDouble() > max.toDouble() -> max
            else -> input
        }
    }
}
