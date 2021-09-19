package com.filipowm.ambassador.configuration.source

import com.filipowm.ambassador.model.source.ProjectSource
import java.util.*

class ProjectSources(private val sources: Map<String, ProjectSource<*>>) {

    fun get(name: String): Optional<ProjectSource<*>> = Optional.ofNullable(sources[name])

    fun getAll(): Collection<ProjectSource<*>> = sources.values
}