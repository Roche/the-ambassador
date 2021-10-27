package com.roche.ambassador.exceptions

open class AmbassadorException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
