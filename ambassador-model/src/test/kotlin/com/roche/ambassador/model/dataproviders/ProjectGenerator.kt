package com.roche.ambassador.model.dataproviders

import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.Visibility
import com.roche.ambassador.model.feature.Features
import com.roche.ambassador.model.project.Permissions
import com.roche.ambassador.model.project.Project
import com.roche.ambassador.model.stats.Statistics

object ProjectGenerator {

    fun generate(features: Features = Features()): Project {
        return Project(
            1, "__url__", "__avatar__", "__name__", "__fullname__",
            "__description__", listOf(), Visibility.PUBLIC, "main",
            false, false, false, Statistics(),
            nowDate(), nowDate(), Permissions(Permissions.Permission.PRIVATE, Permissions.Permission.PRIVATE, Permissions.Permission.PRIVATE, Permissions.Permission.PRIVATE, Permissions.Permission.PRIVATE, Permissions.Permission.PRIVATE),
            features = features
        )
    }

    fun generate(vararg features: Feature<*>): Project {
        return generate(Features(*features))
    }
}
