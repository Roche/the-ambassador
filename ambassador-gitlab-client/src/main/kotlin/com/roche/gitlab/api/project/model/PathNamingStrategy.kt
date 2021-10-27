package com.roche.gitlab.api.project.model

interface PathNamingStrategy {

    fun getPathFor(vararg names: String): String

    companion object {
        fun default(): PathNamingStrategy {
            return DefaultPathNamingStrategy
        }
    }
}

internal object DefaultPathNamingStrategy : PathNamingStrategy {

    private val WHITESPACE_REGEX = "\\s+".toRegex()
    private const val SEPARATOR = "-"

    override fun getPathFor(vararg names: String): String {
        return names
            .map { it.toLowerCase() }
            .joinToString(separator = SEPARATOR) { it.replace(WHITESPACE_REGEX, SEPARATOR) }
    }
}
