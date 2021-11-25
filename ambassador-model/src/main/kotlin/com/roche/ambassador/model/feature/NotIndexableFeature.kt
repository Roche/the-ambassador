package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Importance

abstract class NotIndexableFeature<T>(
    value: T?,
    weight: Double = 1.0,
    importance: Importance = Importance.low()
) : AbstractFeature<T>(value, weight, importance) {
    override fun isIndexable(): Boolean = false
}
