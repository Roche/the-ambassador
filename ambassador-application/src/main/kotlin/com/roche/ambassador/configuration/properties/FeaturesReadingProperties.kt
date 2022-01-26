package com.roche.ambassador.configuration.properties

import com.roche.ambassador.model.Visibility

data class FeaturesReadingProperties(
    val requireVisibility: List<Visibility> = listOf(Visibility.PUBLIC, Visibility.INTERNAL),
)