package pl.filipowm.opensource.ambassador.gradle.utils

import java.io.File
import java.util.*

object PropertiesReader {

    fun readFrom(path: String): Properties {
        val props = Properties()
        File(path).inputStream().use {
            props.load(it)
        }
        return props
    }

}