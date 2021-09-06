package com.filipowm.ambassador.storage

class InvalidSortFieldException(val field: String, message: String, cause: Throwable) : RuntimeException(message, cause)
