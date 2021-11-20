package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Importance
import com.roche.ambassador.model.files.File

abstract class FileFeature<T : File>(
    value: T?,
    weight: Double = 1.0,
    importance: Importance = Importance.low()
) : AbstractFeature<T>(value, weight, importance) {

    override fun exists(): Boolean = value.exists() && value.get().exists

    fun hasSizeAtLeast(size: Long): Boolean = exists() && value.get().hasSizeAtLeast(size)
}
