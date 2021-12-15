package com.roche.ambassador.advisor.messages

import com.roche.ambassador.advisor.common.AdvisorException

class AdviceMessageNotFoundException(val messageKey: String, message: String?, cause: Throwable? = null) : AdvisorException(message, cause)