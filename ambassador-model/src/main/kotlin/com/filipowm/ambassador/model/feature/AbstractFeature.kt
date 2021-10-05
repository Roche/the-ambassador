package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.Explanation
import com.filipowm.ambassador.model.Feature
import com.filipowm.ambassador.model.Importance
import com.filipowm.ambassador.model.Value
import com.filipowm.ambassador.model.utils.Range

abstract class AbstractFeature<T>(
    value: T?, protected val name: String,
    weight: Double = 1.0, protected val importance: Importance = Importance.low()
) : Feature<T> {

    protected val value: Value<T> = Value.of(value)
    protected val weight: Double = WEIGHT_RANGE.adjust(weight)

    companion object {
        private val WEIGHT_RANGE = Range.bound(0.0, 1.0)
    }

    override fun explain(): Explanation {
        if (!value.exists()) {
            return Explanation.no(name)
        }
        return Explanation.no(name)
//        return Explanation.single("Feature $name has value $value")
    }

    override fun weight(): Double = weight

    override fun importance(): Importance = importance

    override fun value(): Value<T> = value

    override fun name(): String = name

    override fun hashCode(): Int = name().hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is Feature<*>) {
            return name() == other.name()
        }
        return false
    }
}