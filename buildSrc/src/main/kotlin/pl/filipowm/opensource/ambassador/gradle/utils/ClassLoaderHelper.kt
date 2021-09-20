package com.filipowm.ambassador.gradle.utils

import java.io.File
import java.net.URL
import java.net.URLClassLoader

object ClassLoaderHelper {

    fun withAdditionalClasspath(jars: Collection<File>, action: () -> Unit) {
        val currentClassloader = Thread.currentThread().contextClassLoader
        try {
            val enhancedLoader = URLClassLoader(arrayOf(), currentClassloader)
            jars.forEach { enhancedLoader.addToClasspath(it) }
            Thread.currentThread().contextClassLoader = enhancedLoader
            action()
        } finally {
            Thread.currentThread().contextClassLoader = currentClassloader
        }
    }

    private fun URLClassLoader.addToClasspath(file: File) {
        val method = this::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(this, file.toURI().toURL())
    }

}