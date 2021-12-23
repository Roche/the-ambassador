package com.roche.ambassador.model.source

import java.util.*

class ProjectSources(private val sources: Map<String, ProjectSource>) {

    fun get(name: String): Optional<ProjectSource> = Optional.ofNullable(sources[name])

    fun getByName(name: String): Optional<ProjectSource> {
        return Optional.ofNullable(sources.values.firstOrNull { it.name() == name })
    }

    fun getAll(): Collection<ProjectSource> = sources.values
}
