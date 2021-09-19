package com.filipowm.ambassador

interface Adapter<T, U> {

    fun convert(value: T?): U

}