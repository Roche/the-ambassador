package com.roche.ambassador.advisor.messages

import com.roche.ambassador.advisor.common.AdvisorConfigurationException

class AdviceMessageNotFoundException(val messageKey: String, message: String?, cause: Throwable? = null) : AdvisorConfigurationException(message, cause)
