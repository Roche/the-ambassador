package com.filipowm.ambassador.model.files

class License(
    var name: String?,
    var key: String?,
    language: String?,
    exists: Boolean,
    hash: String?,
    contentLength: Int?,
    url: String?
) : File(exists, hash, language, contentLength, url) {

    companion object {
        fun notExistent(): License {
            return License(null, null, null, false, null, null, null)
        }
    }
}
