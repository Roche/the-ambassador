package com.roche.ambassador.document

data class Acronym(val acronym: String, val potentialExpansions: List<String>) {

    fun hasExpansions(): Boolean {
        return potentialExpansions.isNotEmpty()
    }
}
