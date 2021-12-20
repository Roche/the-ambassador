package com.roche.ambassador.model.files

import com.roche.ambassador.model.Feature
import com.roche.ambassador.model.feature.ContributingGuideFeature
import com.roche.ambassador.model.feature.LicenseFeature
import com.roche.ambassador.model.feature.ReadmeFeature
import kotlin.reflect.KClass

enum class DocumentType(val feature: KClass<out Feature<out File>>) {

    README(ReadmeFeature::class),
    CONTRIBUTION_GUIDE(ContributingGuideFeature::class),
    LICENSE(LicenseFeature::class)

}