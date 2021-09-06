package com.filipowm.ambassador.model.project

data class Issues(
    val all: Int,
    val open: Int,
    val closed: Int,
    val closedIn90Days: Int,
    val openedIn90Days: Int
)
