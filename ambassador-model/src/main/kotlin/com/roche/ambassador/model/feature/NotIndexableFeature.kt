package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Importance
import com.roche.ambassador.model.IndexEntry

abstract class NotIndexableFeature<T>(
    value: T?,
    name: String,
    weight: Double = 1.0,
    importance: Importance = Importance.low()
) : AbstractFeature<T>(value, name, weight, importance) {
    final override fun asIndexEntry(): IndexEntry = IndexEntry.no()
}
