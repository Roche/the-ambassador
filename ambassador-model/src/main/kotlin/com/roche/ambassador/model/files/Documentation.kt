package com.roche.ambassador.model.files

open class Documentation(
    language: String?,
    hash: String?,
    open var complexity: DocumentationComplexity?,
    exists: Boolean,
    contentLength: Long?,
    url: String?
) : File(exists, hash, language, contentLength, url) {
    companion object {
        fun notExistent(): Documentation {
            return Documentation(
                null,
                null,
                null,
                false,
                null,
                null
            )
        }

        fun create(exists: Boolean, contentLength: Long?): Documentation {
            return Documentation(null, null, null, exists, contentLength, null)
        }
    }
}
