package com.filipowm.gitlab.api.utils.jackson

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import com.filipowm.gitlab.api.model.AccessLevelName
import java.util.*

class GitLabModule : SimpleModule("GitLab API Module", Version.unknownVersion()) {

    init {
        addSerializer(AccessLevelName::class.java, EnumSerialization.toIntSerializer { Optional.ofNullable(it.value) })
        addDeserializer(AccessLevelName::class.java, EnumSerialization.fromIntDeserializer { AccessLevelName.getFromLevel(it) })
    }
}