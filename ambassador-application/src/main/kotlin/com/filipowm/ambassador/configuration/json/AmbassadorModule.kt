package com.filipowm.ambassador.configuration.json

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import com.filipowm.ambassador.model.feature.Features

class AmbassadorModule : SimpleModule("Ambassador API Module", Version.unknownVersion()) {

    init {
        addSerializer(Features::class.java, FeaturesSerializer)
        addDeserializer(Features::class.java, FeaturesDeserializer)
    }
}
