package com.roche.ambassador

interface Identifiable<T> {

    fun getId(): T?
    fun setId(id: T?)
}
