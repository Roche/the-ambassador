package com.filipowm.gitlab.api

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.filipowm.gitlab.api.auth.AnonyomusAuthProvider
import com.filipowm.gitlab.api.auth.PrivateTokenAuthProvider
import com.filipowm.gitlab.api.client.GitLabHttpClient
import com.filipowm.gitlab.api.client.RetryIntervalProvider
import com.filipowm.gitlab.api.exceptions.ExceptionHandler
import com.filipowm.gitlab.api.exceptions.Exceptions
import com.filipowm.gitlab.api.utils.jackson.GitLabModule
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.network.sockets.ConnectTimeoutException
import org.slf4j.LoggerFactory
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeoutException

class GitLabApiBuilder internal constructor() {

    companion object {
        private val log = LoggerFactory.getLogger(GitLab::class.java)
    }

    private var authProvider: AuthProvider = AnonyomusAuthProvider
    private var url: String = "https://gitlab.com"
    private var apiVersion: GitLab.ApiVersion = GitLab.ApiVersion.V4
    private var httpClientBuilder: HttpClientBuilder =
        HttpClientBuilder(this)

    private val retryConfigBuilder = RetryConfig.custom<Any>()
        .retryExceptions(
            HttpRequestTimeoutException::class.java,
            TimeoutException::class.java, ConnectTimeoutException::class.java,
            Exceptions.RequestTimeoutException::class.java,
            Exceptions.RateLimitReachedException::class.java,
            Exceptions.ServerErrorException::class.java,
            CallNotPermittedException::class.java,
            IOException::class.java
        )

    fun url(url: String): GitLabApiBuilder {
        this.url = url
        return this
    }

    fun retry(): RetryBuilder {
        return RetryBuilder(this)
    }

    fun apiVersion(apiVersion: GitLab.ApiVersion): GitLabApiBuilder {
        this.apiVersion = apiVersion
        return this
    }

    fun authenticated(): AuthenticationBuilder {
        return AuthenticationBuilder(this)
    }

    fun httpClient(): HttpClientBuilder {
        return HttpClientBuilder(this)
    }

    fun build(): GitLab {
        val retryRegistry = RetryRegistry.of(retryConfigBuilder.build())
        val gitLabHttpClient = GitLabHttpClient(httpClientBuilder.build(), retryRegistry.retry("gitlab"))
        return GitLabApi(url, "/api/v4", gitLabHttpClient)
    }

    class LoggingBuilder internal constructor(private val httpClientBuilder: HttpClientBuilder) {

        fun logLevel(logLevel: LogLevel): HttpClientBuilder {
            httpClientBuilder.logLevel = logLevel
            return httpClientBuilder
        }

        fun nothing(): HttpClientBuilder {
            return logLevel(LogLevel.NONE)
        }

        fun info(): HttpClientBuilder {
            return logLevel(LogLevel.INFO)
        }

        fun headers(): HttpClientBuilder {
            return logLevel(LogLevel.HEADERS)
        }

        fun body(): HttpClientBuilder {
            return logLevel(LogLevel.BODY)
        }

        fun all(): HttpClientBuilder {
            return logLevel(LogLevel.ALL)
        }
    }

    class CircuitBreakerBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {
        fun build(): GitLabApiBuilder {
            return gitLabApiBuilder
        }
    }

    class RetryBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {
        private var intervalFunctionSupplier: (Duration) -> IntervalFunction = { IntervalFunction.ofExponentialBackoff(it, 2.5, Duration.ofSeconds(60)) }
        private var initialInterval = Duration.ofSeconds(5)
        private var maxAttempts = 5

        fun maxAttempts(maxAttempts: Int): RetryBuilder {
            this.maxAttempts = maxAttempts
            return this
        }

        fun exponentialBackoff(multiplier: Double, maxInterval: Duration): RetryBuilder {
            this.intervalFunctionSupplier = { IntervalFunction.ofExponentialBackoff(it, multiplier, maxInterval)}
            return this
        }

        fun linear(): RetryBuilder {
            this.intervalFunctionSupplier = { IntervalFunction.of(it) }
            return this
        }

        fun build(): GitLabApiBuilder {
            val intervalProvider = RetryIntervalProvider(initialInterval, intervalFunctionSupplier)
            gitLabApiBuilder.retryConfigBuilder
                .intervalBiFunction(intervalProvider)
                .maxAttempts(maxAttempts)
            return gitLabApiBuilder
        }
    }

    class HttpClientBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {
        private var requestTimeout = 10000L
        private var connectTimeout = 10000L
        private var socketTimeout = 10000L
        private var clientThreadsCount = 4
        private var objectMapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(GitLabModule())
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
            .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
        internal var logLevel = LogLevel.NONE
        private val exceptionHandlers: MutableMap<Int, ExceptionHandler> = mutableMapOf()

        fun clientThreadsCount(clientThreadsCount: Int): HttpClientBuilder {
            this.clientThreadsCount = clientThreadsCount
            return this
        }

        /*
        a time period required to process an HTTP call: from sending a request to receiving a response.
         */
        fun requestTimeoutMillis(requestTimeoutMillis: Long): HttpClientBuilder {
            this.requestTimeout = requestTimeoutMillis
            return this
        }

        /*
        a time period in which a client should establish a connection with a server.
        */
        fun connectTimeoutMillis(connectTimeoutMillis: Long): HttpClientBuilder {
            this.connectTimeout = connectTimeoutMillis
            return this
        }

        /*
        a maximum time of inactivity between two data packets when exchanging data with a server.
         */
        fun socketTimeoutMillis(socketTimeoutMillis: Long): HttpClientBuilder {
            this.socketTimeout = socketTimeoutMillis
            return this
        }

        fun handleException(statusCode: HttpStatusCode, handler: ExceptionHandler): HttpClientBuilder {
            this.exceptionHandlers[statusCode.value] = handler
            return this
        }

        fun logging(): LoggingBuilder {
            return LoggingBuilder(this)
        }

        fun and(): GitLabApiBuilder {
            return gitLabApiBuilder
        }

        internal fun build(): HttpClient {
            val exceptionHandler = ExceptionHandler.delegatingTo(exceptionHandlers)
            return HttpClient(CIO) {
                engine {
                    this.threadsCount = clientThreadsCount
                }
                defaultRequest {
                    this.url.takeFrom(URLBuilder().takeFrom(gitLabApiBuilder.url).apply {
                        encodedPath += url.encodedPath
                    })
                }
                install(Auth) {
                    providers.add(gitLabApiBuilder.authProvider)
                }
                install(JsonFeature) {
                    serializer = JacksonSerializer(objectMapper)
                }
                install(Logging) {
                    level = logLevel
                }
                HttpResponseValidator {
                    handleResponseException {
                        val responseException = it as? ResponseException ?: return@handleResponseException
                        exceptionHandler.handle(responseException)
                    }
                }
                install(HttpTimeout) {
                    this.connectTimeoutMillis = connectTimeout
                    this.requestTimeoutMillis = requestTimeout
                    this.socketTimeoutMillis = socketTimeout
                }
            }
        }
    }

    class AuthenticationBuilder internal constructor(private val gitLabApiBuilder: GitLabApiBuilder) {

        fun withPersonalAccessToken(personalAccessToken: String): GitLabApiBuilder {
            gitLabApiBuilder.authProvider = PrivateTokenAuthProvider(personalAccessToken)
            return gitLabApiBuilder
        }

        fun withProjectAccessToken(projectAccessToken: String): GitLabApiBuilder {
            return withPersonalAccessToken(projectAccessToken)
        }

        fun withOAuth2(accessToken: String): GitLabApiBuilder {
            return gitLabApiBuilder
        }

        fun withOAuth2(username: String, password: CharSequence): GitLabApiBuilder {
            return gitLabApiBuilder
        }
    }
}
