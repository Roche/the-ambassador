package com.roche.gitlab.api.utils

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryParam(val name: String = "")
