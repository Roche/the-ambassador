package com.filipowm.ambassador.extensions

import java.time.Duration

fun Duration.toHumanReadable(): String {
    return toString()
        .substring(2)
        .replace("(\\d[HMS])(?!$)".toRegex(), "$1 ")
        .toLowerCase()
}