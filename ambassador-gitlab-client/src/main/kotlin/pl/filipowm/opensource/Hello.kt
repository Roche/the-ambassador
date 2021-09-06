package pl.filipowm.opensource

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

suspend fun main() {
    println("Hello, World")
    val client = HttpClient(CIO)
    var respon: String = client.get("https://google.com")
    print(respon)
}
