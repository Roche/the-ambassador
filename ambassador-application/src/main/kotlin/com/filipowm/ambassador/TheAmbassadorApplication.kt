package com.filipowm.ambassador

import com.filipowm.ambassador.configuration.concurrent.ConcurrencyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [ReactiveUserDetailsServiceAutoConfiguration::class])
@EnableConfigurationProperties
open class TheAmbassadorApplication

fun main(args: Array<String>) {
    runApplication<TheAmbassadorApplication>(*args)
}
