package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApiClient
import org.gitlab4j.api.GitLabApiClientDecorator
import pl.filipowm.opensource.ambassador.exceptions.Exceptions
import java.io.File
import java.net.URL
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

internal class ExceptionHandlingDecorator(wrapped: GitLabApiClient) : GitLabApiClientDecorator(wrapped) {

    override fun get(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = withExceptionHandling { super.get(queryParams, url) }

    override fun get(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = withExceptionHandling { super.get(queryParams, *pathArgs) }

    override fun getWithAccepts(queryParams: MultivaluedMap<String, String>?, accepts: String?, vararg pathArgs: Any?): Response =
        withExceptionHandling { super.getWithAccepts(queryParams, accepts, *pathArgs) }

    override fun getWithAccepts(queryParams: MultivaluedMap<String, String>?, url: URL?, accepts: String?): Response =
        withExceptionHandling { super.getWithAccepts(queryParams, url, accepts) }

    override fun head(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = withExceptionHandling { super.head(queryParams, *pathArgs) }

    override fun head(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = withExceptionHandling { super.head(queryParams, url) }

    override fun post(formData: Form?, vararg pathArgs: Any?): Response = withExceptionHandling { super.post(formData, *pathArgs) }

    override fun post(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = withExceptionHandling { super.post(queryParams, *pathArgs) }

    override fun post(formData: Form?, url: URL?): Response = withExceptionHandling { super.post(formData, url) }

    override fun post(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = withExceptionHandling { super.post(queryParams, url) }

    override fun post(payload: Any?, vararg pathArgs: Any?): Response = withExceptionHandling { super.post(payload, *pathArgs) }

    override fun post(stream: StreamingOutput?, mediaType: String?, vararg pathArgs: Any?): Response = withExceptionHandling { super.post(stream, mediaType, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, vararg pathArgs: Any?): Response =
        withExceptionHandling { super.upload(name, fileToUpload, mediaTypeString, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, formData: Form?, vararg pathArgs: Any?): Response =
        withExceptionHandling { super.upload(name, fileToUpload, mediaTypeString, formData, *pathArgs) }

    override fun upload(name: String?, fileToUpload: File?, mediaTypeString: String?, formData: Form?, url: URL?): Response =
        withExceptionHandling { super.upload(name, fileToUpload, mediaTypeString, formData, url) }

    override fun putUpload(name: String?, fileToUpload: File?, vararg pathArgs: Any?): Response = withExceptionHandling { super.putUpload(name, fileToUpload, *pathArgs) }

    override fun putUpload(name: String?, fileToUpload: File?, url: URL?): Response = withExceptionHandling { super.putUpload(name, fileToUpload, url) }

    override fun put(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = withExceptionHandling { super.put(queryParams, *pathArgs) }

    override fun put(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = withExceptionHandling { super.put(queryParams, url) }

    override fun put(formData: Form?, vararg pathArgs: Any?): Response = withExceptionHandling { super.put(formData, *pathArgs) }

    override fun put(formData: Form?, url: URL?): Response = withExceptionHandling { super.put(formData, url) }

    override fun put(payload: Any?, vararg pathArgs: Any?): Response = withExceptionHandling { super.put(payload, *pathArgs) }

    override fun delete(queryParams: MultivaluedMap<String, String>?, vararg pathArgs: Any?): Response = withExceptionHandling { super.delete(queryParams, *pathArgs) }

    override fun delete(queryParams: MultivaluedMap<String, String>?, url: URL?): Response = withExceptionHandling { super.delete(queryParams, url) }

    private fun withExceptionHandling(handler: () -> Response): Response {
        val response = handler()
        if (response.status >= 400) {
            Exceptions.getExceptionForStatus(response.status).ifPresent { throw it }
        }
        return response
    }

}