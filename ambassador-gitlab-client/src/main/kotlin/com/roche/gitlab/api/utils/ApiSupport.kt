package com.roche.gitlab.api.utils

import com.roche.gitlab.api.client.GitLabHttpClient
import com.roche.gitlab.api.exceptions.Exceptions
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaType

const val PAGE_HEADER: String = "x-page"
const val PER_PAGE_HEADER: String = "x-per-page"
const val TOTAL_ELEMENTS_HEADER: String = "x-total"
const val TOTAL_PAGES_HEADER: String = "x-total-pages"
const val NEXT_PAGE: String = "x-next-page"

fun HttpResponse.getPageInfo(): PageInfo {
    val page = headers.getOptionalHeaderAsInt(PAGE_HEADER).orElse(-1)
    val perPage = headers.getOptionalHeaderAsInt(PER_PAGE_HEADER).orElse(-1)
    val total = headers.getOptionalHeaderAsInt(TOTAL_ELEMENTS_HEADER).orElse(-1)
    val totalPages = headers.getOptionalHeaderAsInt(TOTAL_PAGES_HEADER).orElse(-1)
    val nextPage = headers.getOptionalHeaderAsInt(NEXT_PAGE).orElse(-1)
    return PageInfo.of(page, total, totalPages, perPage, nextPage)
}

fun HttpRequestBuilder.applyQueryParameters(vararg inputs: Any) {
    for (input in inputs) {
        input::class.declaredMemberProperties
            .filter { it.hasAnnotation<QueryParam>() }
            .forEach {
                val annotation = it.findAnnotation<QueryParam>() ?: return@forEach
                val key = if (annotation.name.isNotBlank()) annotation.name else it.name
                val value = it.getter.call(input)
                if (value == null) {
                    parameter(key, value)
                } else {
                    val parsedValue = parseValue(value)
                    parameter(key, parsedValue)
                }
            }
    }
}

fun parseValue(value: Any): Any {
    if (value.javaClass.isEnum) {
        return (value as Enum<*>).name.lowercase()
    }
    return value
}

suspend inline fun <reified T> HttpResponse.receiveWithExpandedErasure(): T {
    val type = typeInfo<T>()
    val reifiedType = Optional.ofNullable(type.kotlinType?.javaType).orElse(type.reifiedType)
    val reworkedType = TypeInfoImpl(type.type, reifiedType, type.kotlinType)
    return call.receive(reworkedType) as T
}

data class TypeInfoImpl(
    override val type: KClass<*>,
    override val reifiedType: Type,
    override val kotlinType: KType? = null
) : TypeInfo

suspend inline fun <reified T> GitLabHttpClient.getPage(
    path: String,
    pagination: Pagination,
    noinline block: HttpRequestBuilder.() -> Unit = {}
): Page<T> {
    val response: HttpResponse = this.get(path = path) {
        block.invoke(this)
        applyQueryParameters(pagination)
    }
    val content: List<T> = response.receiveWithExpandedErasure() as List<T>
    val pageInfo = response.getPageInfo()
    return Page(content, pageInfo)
}

suspend inline fun <reified T> GitLabHttpClient.getList(
    path: String,
    noinline block: HttpRequestBuilder.() -> Unit = {}
): List<T> {
    val response: HttpResponse = this.get(path = path) {
        block.invoke(this)
    }
    return response.receiveWithExpandedErasure()
}

@SuppressWarnings("SwallowedException")
suspend inline fun <reified T> GitLabHttpClient.optionally(action: GitLabHttpClient.() -> T): Optional<T> {
    return try {
        Optional.ofNullable(action.invoke(this))
    } catch (ex: ClientRequestException) {
        if (ex.response.status == HttpStatusCode.NotFound) {
            return Optional.empty()
        }
        throw ex
    } catch (ex: Exceptions.NotFoundException) {
        return Optional.empty()
    }
}

fun Headers.getOptionalHeader(name: String): Optional<String> = Optional.ofNullable(this[name])
fun Headers.getOptionalHeaderAsInt(name: String): Optional<Int> = getOptionalHeader(name).map { it.toIntOrNull() }
