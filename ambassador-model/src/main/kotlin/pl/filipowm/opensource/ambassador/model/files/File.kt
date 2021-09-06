package pl.filipowm.opensource.ambassador.model.files

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
