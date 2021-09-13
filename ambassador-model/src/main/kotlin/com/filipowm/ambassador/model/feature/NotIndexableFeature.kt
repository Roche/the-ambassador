package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.Importance
import com.filipowm.ambassador.model.IndexEntry

abstract class NotIndexableFeature<T>(
    value: T?, name: String,
    weight: Double = 1.0, importance: Importance = Importance.low()
) : AbstractFeature<T>(value, name, weight, importance) {
    final override fun asIndexEntry() = IndexEntry.no()
}