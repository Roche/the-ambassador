package org.gitlab4j.api

import java.io.File
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

/**
 * This class was created in org.gitlab4j.api package, because GitlabApiClient does not support
 * any way of handy extending / decorating behavior. Also that's the cause why dummy values are used
 * and any calls are sent to wrapped client.
 */
abstract class GitLabApiClientDecorator(private val wrapped: GitLabApiClient) : GitLabApiClient("http://dummy", "dummy") {

    protected abstract fun decorate(handler: () -> Response): Response

    override fun close() {
        wrapped.close()
    }

    override fun enableRequestResponseLogging(logger: Logger?, level: Level?, maxEntityLength: Int, maskedHeaderNames: MutableList<String>?) {
        wrapped.enableRequestResponseLogging(logger, level, maxEntityLength, maskedHeaderNames)
    }

    override fun setRequestTimeout(connectTimeout: Int?, readTimeout: Int?) {
        wrapped.setRequestTimeout(connectTimeout, readTimeout)
    }

    override fun getAuthToken(): String {
        return wrapped.getAuthToken()
    }

    override fun getSecretToken(): String {
        return wrapped.getSecretToken()
    }

    override fun getTokenType(): Constants.TokenType {
        return wrapped.getTokenType()
    }

    override fun getSudoAsId(): Int {
        return wrapped.getSudoAsId()
    }

    override fun setSudoAsId(sudoAsId: Int?) {
        wrapped.setSudoAsId(sudoAsId)
    }

    override fun getApiUrl(vararg pathArgs: Any?): URL {
        return wrapped.getApiUrl(*pathArgs)
    }

    override fun getUrlWithBase(vararg pathArgs: Any?): URL {
        return wrapped.getUrlWithBase(*pathArgs)
    }

    override fun validateSecretToken(response: Response?): Boolean {
        return wrapped.validateSecretToken(response)
    }

    override fun get(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = decorate { wrapped.get(queryParams, url) }

    override fun get(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = decorate { wrapped.get(queryParams, *pathArgs) }

    override fun getWithAccepts(queryParams: MultivaluedMap<String, String>?, accepts: String?, vararg pathArgs: Any?): Response =
        decorate { wrapped.getWithAccepts(queryParams, accepts, *pathArgs) }

    override fun getWithAccepts(queryParams: MultivaluedMap<String, String>?, url: URL?, accepts: String?): Response =
        decorate { wrapped.getWithAccepts(queryParams, url, accepts) }

    override fun head(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = decorate { wrapped.head(queryParams, *pathArgs) }

    override fun head(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = decorate { wrapped.head(queryParams, url) }

    override fun post(formData: Form?, vararg pathArgs: Any?): Response = decorate { wrapped.post(formData, *pathArgs) }

    override fun post(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = decorate { wrapped.post(queryParams, *pathArgs) }

    override fun post(formData: Form?, url: URL?): Response = decorate { wrapped.post(formData, url) }

    override fun post(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = decorate { wrapped.post(queryParams, url) }

    override fun post(payload: Any?, vararg pathArgs: Any?): Response = decorate { wrapped.post(payload, *pathArgs) }

    override fun post(stream: StreamingOutput?, mediaType: String?, vararg pathArgs: Any?): Response = decorate { wrapped.post(stream, mediaType, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, vararg pathArgs: Any?): Response =
        decorate { wrapped.upload(name, fileToUpload, mediaTypeString, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, formData: Form?, vararg pathArgs: Any?): Response =
        decorate { wrapped.upload(name, fileToUpload, mediaTypeString, formData, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, formData: Form?, url: URL?): Response =
        decorate { wrapped.upload(name, fileToUpload, mediaTypeString, formData, url) }

    override fun putUpload(name: String?, fileToUpload: File?, vararg pathArgs: Any?): Response = decorate { wrapped.putUpload(name, fileToUpload, *pathArgs) }

    override fun putUpload(name: String?, fileToUpload: File?, url: URL?): Response = decorate { wrapped.putUpload(name, fileToUpload, url) }

    override fun put(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = decorate { wrapped.put(queryParams, *pathArgs) }

    override fun put(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = decorate { wrapped.put(queryParams, url) }

    override fun put(formData: Form?, vararg pathArgs: Any?): Response = decorate { wrapped.put(formData, *pathArgs) }

    override fun put(formData: Form?, url: URL?): Response = decorate { wrapped.put(formData, url) }

    override fun put(payload: Any?, vararg pathArgs: Any?): Response = decorate { wrapped.put(payload, *pathArgs) }

    override fun delete(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = decorate { wrapped.delete(queryParams, *pathArgs) }

    override fun delete(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = decorate { wrapped.delete(queryParams, url) }

    override fun invocation(url: URL?, queryParams: MultivaluedMap<String, String>?): Invocation.Builder {
        return wrapped.invocation(url, queryParams)
    }

    override fun invocation(url: URL?, queryParams: MultivaluedMap<String, String>?, accept: String?): Invocation.Builder {
        return wrapped.invocation(url, queryParams, accept)
    }

    override fun createApiClient(): Client {
        return wrapped.createApiClient()
    }

    override fun setHostUrlToBaseUrl() {
        wrapped.setHostUrlToBaseUrl()
    }

    override fun getIgnoreCertificateErrors(): Boolean {
        return wrapped.getIgnoreCertificateErrors()
    }

    override fun setIgnoreCertificateErrors(ignoreCertificateErrors: Boolean) {
        wrapped.setIgnoreCertificateErrors(ignoreCertificateErrors)
    }
}