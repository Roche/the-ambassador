package pl.filipowm.opensource.ambassador.gitlab.api

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.GitLabApiClient

class GitlabApiDecorator(private val hostUrl: String, private val token: String, clientDecorator: (GitLabApiClient) -> GitLabApiClient) : GitLabApi(hostUrl, token) {

    init {
        // there is not better way in Gitlab4j library to override behavior of api client
        val field = this.javaClass.superclass.getDeclaredField("apiClient")
        if (field.trySetAccessible()) {
            val originalClient = field.get(this) as GitLabApiClient
            val decoratedClient = clientDecorator.invoke(originalClient)
            field.set(this, decoratedClient)
        }
    }
}
