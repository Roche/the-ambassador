package com.roche.ambassador.advisor.common

import com.roche.ambassador.exceptions.AmbassadorException

open class AdvisorException(message: String? = null, throwable: Throwable? = null) : AmbassadorException(message, throwable)

open class AdvisorConfigurationException(message: String? = null, throwable: Throwable? = null) : AdvisorException(message, throwable)
