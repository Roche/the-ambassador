package com.roche.ambassador.storage

import com.roche.ambassador.exceptions.AmbassadorException

class InvalidSortFieldException(val field: String, message: String, cause: Throwable) : AmbassadorException(message, cause)
