package com.filipowm.ambassador.storage

import com.filipowm.ambassador.exceptions.AmbassadorException

class InvalidSortFieldException(val field: String, message: String, cause: Throwable) : AmbassadorException(message, cause)
