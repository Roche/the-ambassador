package com.filipowm.ambassador.fake

class Probable<T>(val value: T, val probability: Float) {

    init {
        if (probability <= 0 || probability > 1) {
            throw IllegalStateException("Probability must be greater than 0 and not more than 1")
        }
    }
}
