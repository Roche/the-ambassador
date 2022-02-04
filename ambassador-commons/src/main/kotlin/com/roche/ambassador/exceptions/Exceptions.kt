package com.roche.ambassador.exceptions

object Exceptions {

    open class NotFoundException(message: String?, cause: Throwable? = null) : AmbassadorException(message, cause)
    open class IndexingException(message: String?, cause: Throwable? = null) : AmbassadorException(message, cause)
}
