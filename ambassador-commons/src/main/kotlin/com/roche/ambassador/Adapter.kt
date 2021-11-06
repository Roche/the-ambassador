package com.roche.ambassador

interface Adapter<T, U> {

    fun convert(value: T?): U
}
