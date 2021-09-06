package com.filipowm.ambassador.model.utils

object Functions {

   fun <T> withNotNull(input: T?, func: (T) -> Double): Double {
       return if (input != null) {
           func(input)
       } else {
           0.0
       }
   }
}
