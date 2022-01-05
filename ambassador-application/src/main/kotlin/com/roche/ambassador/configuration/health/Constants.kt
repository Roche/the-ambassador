package com.roche.ambassador.configuration.health

import org.springframework.boot.actuate.health.Health

internal object Constants {

    val UP: Health = Health.up().build()
    val DOWN: Health = Health.down().build()

}