package com.roche.ambassador.model.source

interface ProjectDetailsResolver<T> {

    fun resolveName(project: T): String
    fun resolveId(project: T): String
}