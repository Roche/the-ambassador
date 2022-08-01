package com.roche.ambassador.security.configuration

import com.roche.ambassador.extensions.LoggerDelegate
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.web.server.savedrequest.ServerRequestCache
import org.springframework.security.web.server.util.matcher.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.*

internal class RedirectUriAwareCookieServerRequestCache(private val allowedRedirectUris: List<String> = listOf()) : ServerRequestCache {

    companion object {
        const val REDIRECT_URI_COOKIE_NAME: String = "REDIRECT_URI"
        const val REDIRECT_URI_PARAMETER: String = "redirect_uri"
        val COOKIE_MAX_AGE: Duration = Duration.ofSeconds(-1)
        private val log by LoggerDelegate()
    }

    private var saveRequestMatcher: ServerWebExchangeMatcher = createDefaultRequestMatcher()

    override fun saveRequest(exchange: ServerWebExchange): Mono<Void> {
        return saveRequestMatcher.matches(exchange)
            .filter { it.isMatch }
            .map { exchange.response }
            .map { it.cookies }
            .doOnNext {
                val redirectUriCookie: ResponseCookie = createRedirectUriCookie(exchange.request)
                if (redirectUriCookie.value.isNotEmpty()) {
                    it.add(REDIRECT_URI_COOKIE_NAME, redirectUriCookie)
                }
                log.debug("Request added to Cookie: {}")
            }.then()
    }

    override fun getRedirectUri(exchange: ServerWebExchange): Mono<URI> {
        val cookieMap = exchange.request.cookies
        return Mono.justOrEmpty(cookieMap.getFirst(REDIRECT_URI_COOKIE_NAME))
            .map { it.value }
            .map { decodeCookie(it) }
            .onErrorResume(IllegalArgumentException::class.java) { Mono.empty() }
            .map { URI.create(it) }
    }

    override fun removeMatchingRequest(exchange: ServerWebExchange): Mono<ServerHttpRequest> {
        return Mono.just(exchange.response).map { obj: ServerHttpResponse -> obj.cookies }
            .doOnNext {
                val invalidateCookie = invalidateRedirectUriCookie(exchange.request)
                it.add(REDIRECT_URI_COOKIE_NAME, invalidateCookie)
            }
            .thenReturn(exchange.request)
    }

    private fun createRedirectUriCookie(request: ServerHttpRequest): ResponseCookie {
        val path = request.path.pathWithinApplication().value()
        val query = request.uri.rawQuery
        val redirectUriParam = request.queryParams.getOrDefault(REDIRECT_URI_PARAMETER, listOf())
        val redirectUriParamWithoutQuery = redirectUriParam
            .map { URI.create(it) }
            .map { URI(it.scheme, it.authority, it.path, null, it.fragment).toString() }
            .firstOrNull()

        val redirectUri = if (redirectUriParamWithoutQuery != null && (redirectUriParamWithoutQuery.startsWith("/") || redirectUriParamWithoutQuery in allowedRedirectUris)) {
            redirectUriParam.first()
        } else {
            path + if (query != null) "?$query" else ""
        }
        return createResponseCookie(request, encodeCookie(redirectUri), COOKIE_MAX_AGE)
    }

    private fun invalidateRedirectUriCookie(request: ServerHttpRequest): ResponseCookie {
        return createResponseCookie(request, null, Duration.ZERO)
    }

    private fun createResponseCookie(request: ServerHttpRequest, cookieValue: String?, age: Duration): ResponseCookie {
        return ResponseCookie.from(REDIRECT_URI_COOKIE_NAME, cookieValue.orEmpty())
            .path(request.path.contextPath().value() + "/")
            .maxAge(age)
            .httpOnly(true)
            .secure("https".equals(request.uri.scheme, ignoreCase = true)).sameSite("Lax").build()
    }

    private fun encodeCookie(cookieValue: String): String {
        return String(Base64.getEncoder().encode(cookieValue.toByteArray()))
    }

    private fun decodeCookie(encodedCookieValue: String): String {
        return String(Base64.getDecoder().decode(encodedCookieValue.toByteArray()))
    }

    private fun createDefaultRequestMatcher(): ServerWebExchangeMatcher {
        val get = ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/**")
        val notFavicon: ServerWebExchangeMatcher = NegatedServerWebExchangeMatcher(
            ServerWebExchangeMatchers.pathMatchers("/favicon.*")
        )
        val html = MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML)
        html.setIgnoredMediaTypes(setOf(MediaType.ALL))
        return AndServerWebExchangeMatcher(get, notFavicon, html)
    }

    private fun noneMatcher(): ServerWebExchangeMatcher = ServerWebExchangeMatcher {
        ServerWebExchangeMatcher.MatchResult.notMatch()
    }
}
