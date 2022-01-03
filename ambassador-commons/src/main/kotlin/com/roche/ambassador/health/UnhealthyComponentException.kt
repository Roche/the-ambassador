package com.roche.ambassador.health

import com.roche.ambassador.exceptions.AmbassadorException

class UnhealthyComponentException(val status: Status, message: String?, cause: Throwable?) : AmbassadorException(message, cause) {
    enum class Status {
        UNAUTHORIZED,
        UNAVAILABLE,
        UNKNOWN,
        RATE_LIMITED
    }
}