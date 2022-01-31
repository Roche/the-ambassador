package com.roche.ambassador.lookups

import com.roche.ambassador.storage.Lookup

data class LookupDto(val name: String, val count: Long)

fun Lookup.toDto(): LookupDto {
    return LookupDto(getName(), getCount())
}