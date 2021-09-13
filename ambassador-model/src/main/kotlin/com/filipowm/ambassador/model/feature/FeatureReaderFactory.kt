package com.filipowm.ambassador.model.feature

import com.filipowm.ambassador.model.Feature
import com.filipowm.ambassador.model.FeatureReader

interface FeatureReaderFactory<T: Feature<*>> {

    fun create(): FeatureReader<T>

}