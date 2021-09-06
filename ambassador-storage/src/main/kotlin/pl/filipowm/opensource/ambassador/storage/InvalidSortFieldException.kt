package pl.filipowm.opensource.ambassador.storage

class InvalidSortFieldException(val field: String, message: String, cause: Throwable) : RuntimeException(message, cause)
