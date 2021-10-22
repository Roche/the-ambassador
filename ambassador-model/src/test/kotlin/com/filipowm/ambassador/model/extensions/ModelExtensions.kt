package com.filipowm.ambassador.model.extensions

import com.devskiller.jfairy.Fairy
import com.filipowm.ambassador.model.files.Documentation
import com.filipowm.ambassador.model.files.ExcerptFile
import com.filipowm.ambassador.model.files.RawFile
import java.util.*

val fairy = Fairy.create(Locale.ENGLISH)

fun Documentation.toExcerptFile(): ExcerptFile = toRawFile().asExcerptFile()

fun Documentation.toRawFile(): RawFile {
    return RawFile(exists, hash, language, contentLength, url, generateContent(contentLength))
}

private fun generateContent(size: Long?): String? {
    if (size == null || size <= 0) {
        return null
    }
    return fairy.textProducer().word((size / 2).toInt()).substring(0, (size - 1).toInt())
}