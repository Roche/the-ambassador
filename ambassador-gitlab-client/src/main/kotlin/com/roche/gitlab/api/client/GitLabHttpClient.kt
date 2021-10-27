package com.roche.gitlab.api.client

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*

class GitLabHttpClient(
    val client: HttpClient,
    val retry: Retry
) {

    suspend inline fun <reified T> withCircuitBreaker(noinline block: suspend () -> T): T {
        return retry.executeSuspendFunction(block)
    }

    suspend inline fun <reified T> get(builder: HttpRequestBuilder): T = withCircuitBreaker { client.get(builder) }

    suspend inline fun <reified T> get(
        scheme: String = "http",
        host: String = "localhost",
        port: Int = DEFAULT_PORT,
        path: String = "/",
        body: Any = EmptyContent,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): T = withCircuitBreaker { client.get(scheme, host, port, path, body, block) }

    suspend inline fun <reified T> get(
        urlString: String,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): T = withCircuitBreaker { client.get(urlString, block) }

    suspend inline fun <reified T> post(builder: HttpRequestBuilder): T = withCircuitBreaker { client.post(builder) }

    suspend inline fun <reified T> post(
        scheme: String = "http",
        host: String = "localhost",
        port: Int = DEFAULT_PORT,
        path: String = "/",
        body: Any = EmptyContent,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): T = withCircuitBreaker { client.post(scheme, host, port, path, body, block) }

    suspend inline fun <reified T> delete(
        scheme: String = "http",
        host: String = "localhost",
        port: Int = DEFAULT_PORT,
        path: String = "/",
        body: Any = EmptyContent,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): T = withCircuitBreaker { client.delete(scheme, host, port, path, body, block) }
}
