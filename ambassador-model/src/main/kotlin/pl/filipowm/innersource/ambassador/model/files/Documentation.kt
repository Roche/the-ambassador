package pl.filipowm.innersource.ambassador.model.files

open class Documentation(
        var language: String?,
        var length: Int?,
        var hash: String?,
        var complexity: DocumentationComplexity?,
        var exists: Boolean
) {
    companion object {
        fun notExistent(): Documentation {
            return Documentation(null, 0, null, DocumentationComplexity(0f), false)
        }
    }
}

class License(
        language: String?,
        length: Int?,
        hash: String?,
        complexity: DocumentationComplexity?,
        licenseName: String?,
        licenseKey: String?,
        exists: Boolean) : Documentation(language, length, hash, complexity, exists) {
    companion object {
        fun notExistent(): License {
            return License(null, 0, null, DocumentationComplexity(0f), null, null, false)
        }
    }
}