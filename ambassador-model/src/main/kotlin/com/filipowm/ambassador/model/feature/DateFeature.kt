package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.extensions.daysUntilNow
import com.filipowm.ambassador.model.Importance
import java.time.LocalDate

open class DateFeature(
    value: LocalDate?, name: String,
    weight: Double = 1.0, importance: Importance = Importance.low()
) : AbstractFeature<LocalDate>(value, name, weight, importance) {

    fun daysUntilNow(): Long? = if (value.exists()) {
        value.get().daysUntilNow()
    } else {
        null
    }
}