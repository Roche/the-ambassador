package pl.filipowm.innersource.ambassador

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
open class TheAmbassadorApplication

fun main(args: Array<String>) {
    runApplication<TheAmbassadorApplication>(*args)
}
