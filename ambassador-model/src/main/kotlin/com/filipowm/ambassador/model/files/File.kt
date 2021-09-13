package com.filipowm.ambassador.model.files

import com.fasterxml.jackson.annotation.JsonIgnore

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

class RawFile(exists: Boolean, hash: String?, language: String?, contentLength: Int?, url: String?,
              @JsonIgnore
              private val content: String?) : File(exists, hash, language, contentLength, url) {

    companion object {
        fun notExistent(): RawFile {
            return RawFile(false, null, null, null, null, null)
        }
    }
}
