package com.filipowm.ambassador.model.files

import com.fasterxml.jackson.annotation.JsonIgnore
import com.filipowm.ambassador.extensions.substringWithFullWords

open class File(
    var exists: Boolean,
    var hash: String?,
    var language: String?,
    var contentLength: Int?,
    var url: String?
) {
    companion object {
        fun notExistent(): File {
            return File(false, null, null, null, null)
        }
    }
}

open class RawFile(
    exists: Boolean, hash: String?, language: String?, contentLength: Int?, url: String?,
    @JsonIgnore
    private val content: String?
) : File(exists, hash, language, contentLength, url) {

    fun asExcerptFile(): ExcerptFile {
        val excerpt = content?.substringWithFullWords(0, EXCERPT_MAX_SIZE, ' ', '\n')
        return ExcerptFile(exists, hash, language, contentLength, url, excerpt)
    }

    companion object {
        const val EXCERPT_MAX_SIZE: Int = 1000
        fun notExistent(): RawFile {
            return RawFile(false, null, null, null, null, null)
        }
    }
}

open class ExcerptFile(
    exists: Boolean, hash: String?, language: String?,
    contentLength: Int?, url: String?, val excerpt: String?
) : File(exists, hash, language, contentLength, url)
