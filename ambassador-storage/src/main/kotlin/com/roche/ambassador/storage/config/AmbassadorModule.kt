package com.roche.ambassador.storage.config

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import com.roche.ambassador.model.Score
import com.roche.ambassador.model.feature.Features

class AmbassadorModule : SimpleModule("Ambassador API Module", Version.unknownVersion()) {

    init {
        addSerializer(Features::class.java, FeaturesSerializer)
        addDeserializer(Features::class.java, FeaturesDeserializer)
        addDeserializer(Score::class.java, ScoreDeserializer)
    }
}
