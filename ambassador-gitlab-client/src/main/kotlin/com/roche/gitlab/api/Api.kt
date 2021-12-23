package com.roche.gitlab.api

import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.utils.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*
import java.util.stream.Stream

abstract class Api(val basePath: String, val client: GitLabHttpClient) {

    protected suspend inline fun <reified T> doGet(
        path: String = "",
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf()
    ): T {
        return client.get(path = "$basePath/$path") {
            params.apply(this::parameter)
            headers.apply(this::header)
        }
    }

    protected suspend inline fun <reified T> doGet(vararg inputs: Any): T {
        return client.get(path = basePath) {
            this.applyQueryParameters(*inputs)
        }
    }

    protected suspend inline fun <reified T> doGetOptional(vararg inputs: Any): Optional<T> {
        return client.optionally {
            get(path = basePath) {
                this.applyQueryParameters(*inputs)
            }
        }
    }

    protected suspend inline fun <reified T> doGetOptional(
        path: String = "",
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf()
    ): Optional<T> {
        return client.optionally {
            get(path = "$basePath/$path") {
                params.apply(this::parameter)
                headers.apply(this::header)
            }
        }
    }

    protected suspend inline fun <reified T> doGetPage(path: String, pagination: Pagination, vararg inputs: Any): Page<T> {
        return client.getPage(path = "$basePath/$path", pagination) {
            applyQueryParameters(*inputs)
        }
    }

    protected suspend inline fun <reified T> doGetPage(pagination: Pagination, vararg inputs: Any): Page<T> = doGetPage("", pagination, *inputs)

    protected suspend inline fun <reified T> doGetList(vararg inputs: Any): List<T> {
        return client.getList(path = basePath) {
            this.applyQueryParameters(*inputs)
        }
    }

    protected suspend inline fun <reified T> doGetStream(vararg inputs: Any): Stream<T> {
        val list = doGetList<T>(inputs)
        return list.stream()
    }

    protected suspend inline fun <reified T> doGetList(path: String = "", vararg inputs: Any): List<T> {
        return client.getList(path = "$basePath/$path") {
            this.applyQueryParameters(*inputs)
        }
    }

    protected suspend inline fun <reified T> doPostSelf(
        path: String = "",
        body: T,
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf(),
        contentType: ContentType = ContentType.Application.Json
    ): T {
        return doPost(path, body as Any, params, headers, contentType)
    }

    protected suspend inline fun <reified T> doPost(
        path: String = "",
        body: Any,
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf(),
        contentType: ContentType = ContentType.Application.Json
    ): T {
        return client.post(path = "$basePath/$path") {
            contentType(contentType)
            params.apply(this::parameter)
            headers.apply(this::header)
            this.body = body
        }
    }

    protected suspend inline fun <reified T> doPut(
        path: String = "",
        body: Any,
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf(),
        contentType: ContentType = ContentType.Application.Json
    ): T {
        return client.put(path = "$basePath/$path") {
            contentType(contentType)
            params.apply(this::parameter)
            headers.apply(this::header)
            this.body = body
        }
    }

    protected suspend inline fun <reified T> doDelete(
        path: String = "",
        params: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf()
    ) {
        return client.delete(path = "$basePath/$path") {
            params.apply(this::parameter)
            headers.apply(this::header)
        }
    }

    protected fun <T, U> Map<T, U?>.apply(handler: (T, U?) -> Unit) {
        forEach(handler)
    }
}
