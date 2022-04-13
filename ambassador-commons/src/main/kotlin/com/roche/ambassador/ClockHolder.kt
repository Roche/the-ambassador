package com.roche.ambassador

import java.time.Clock

// TODO make setter thread-safe
object ClockHolder {

    var clock: Clock = Clock.systemDefaultZone()

}