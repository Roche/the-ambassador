package pl.filipowm.opensource.ambassador.model.files

open class Documentation(
    language: String?,
    hash: String?,
    open var complexity: DocumentationComplexity?,
    exists: Boolean,
    contentLength: Int?,
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
    }
}

