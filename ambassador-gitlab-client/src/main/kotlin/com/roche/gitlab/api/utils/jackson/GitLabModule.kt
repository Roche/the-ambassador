package com.roche.gitlab.api.utils.jackson

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.module.SimpleModule
import com.roche.gitlab.api.model.AccessLevelName
import com.roche.gitlab.api.project.events.Action
import com.roche.gitlab.api.project.events.TargetType
import java.util.*

class GitLabModule : SimpleModule("GitLab API Module", Version.unknownVersion()) {

    init {
        addSerializer(AccessLevelName::class.java, EnumSerialization.toIntSerializer { Optional.ofNullable(it.value) })
        addDeserializer(AccessLevelName::class.java, EnumSerialization.fromIntDeserializer { AccessLevelName.getFromLevel(it) })
        addSerializer(Action::class.java, EnumSerialization.toStringSerializer { Optional.ofNullable(it.value) })
        addDeserializer(Action::class.java, EnumSerialization.fromStringDeserializer { Action.from(it) })
        addSerializer(TargetType::class.java, EnumSerialization.toStringSerializer { Optional.ofNullable(it.value) })
        addDeserializer(TargetType::class.java, EnumSerialization.fromStringDeserializer { TargetType.from(it) })
    }
}
