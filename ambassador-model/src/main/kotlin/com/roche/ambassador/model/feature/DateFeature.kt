package com.roche.ambassador.model.feature

import com.roche.ambassador.extensions.daysUntilNow
import com.roche.ambassador.model.Importance
import java.time.LocalDate

abstract class DateFeature(
    value: LocalDate?,
    weight: Double = 1.0,
    importance: Importance = Importance.low()
) : AbstractFeature<LocalDate>(value, weight, importance) {

    fun daysUntilNow(): Long? = if (value.exists()) {
        value.get().daysUntilNow()
    } else {
        null
    }
}
