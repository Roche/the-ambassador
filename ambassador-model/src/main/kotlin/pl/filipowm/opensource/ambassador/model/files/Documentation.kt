package pl.filipowm.opensource.ambassador.model.files

open class Documentation(
    open var language: String?,
    open var length: Int?,
    open var hash: String?,
    open var complexity: DocumentationComplexity?,
    open var exists: Boolean
) {
    companion object {
        fun notExistent(): Documentation {
            return Documentation(null, null, null, null, false)
        }
    }
}

