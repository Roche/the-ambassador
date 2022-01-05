package com.roche.ambassador.configuration.health

import org.springframework.boot.actuate.health.Health

internal object Constants {

    val UP = Health.up().build()
    val DOWN = Health.down().build()

}