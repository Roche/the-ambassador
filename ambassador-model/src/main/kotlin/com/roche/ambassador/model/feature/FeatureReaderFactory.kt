package com.roche.ambassador.model.feature

import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.FeatureReader

interface FeatureReaderFactory<T : Feature<*>> {

    fun create(): FeatureReader<T>
}
