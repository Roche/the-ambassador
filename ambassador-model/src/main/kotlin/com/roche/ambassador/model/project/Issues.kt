package com.roche.ambassador.model.project

data class Issues(
    val all: Int,
    val open: Int,
    val closed: Int,
    val allIn90Days: Int,
    val closedIn90Days: Int,
    val openedIn90Days: Int
)
